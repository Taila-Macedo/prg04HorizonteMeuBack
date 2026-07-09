package br.com.ifba.horizontemeu.foto.service;

import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.foto.repository.FotoRepository;
import br.com.ifba.horizontemeu.notificacao.enums.TipoNotificacao;
import br.com.ifba.horizontemeu.notificacao.service.NotificacaoIService;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.repository.PontoTuristicoRepository;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FotoService implements FotoIService{

    private final FotoRepository fotoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;
    // NOVO — usado para notificar o autor da foto quando ela for aprovada (RN24)
    private final NotificacaoIService notificacaoIService;

    private static final Logger log = LoggerFactory.getLogger(FotoService.class);

    @Override
    public Page<Foto> findAll(Pageable pageable) {
        log.info("Buscando todas as fotos...");
        return fotoRepository.findAll(pageable);
    }

    @Override
    public Optional<Foto> findById(Long id) {
        log.info("Buscando foto com id: {}", id);
        return fotoRepository.findById(id);
    }

    // Busca todas as fotos de um ponto turístico
    @Override
    public List<Foto> findByPontoTuristico(Long idPonto) {
        log.info("Buscando fotos do ponto turístico id: {}", idPonto);

        // Se o ponto não existir retorna lista vazia em vez de lançar exceção
        return pontoTuristicoRepository.findById(idPonto)
                .map(fotoRepository::findByPontoTuristico)
                .orElse(List.of());
    }

    @Override
    public List<Foto> findByAprovado(Boolean aprovado) {
        log.info("Buscando fotos com aprovado: {}", aprovado);
        return fotoRepository.findByAprovado(aprovado);
    }

    @Transactional
    @Override
    public Foto save(Foto foto) {
        if(foto == null) {
            throw new BusinessException("dados da foto não preenchidos.");
        } if(foto.getId() != null) {
            throw new BusinessException("Foto já existente.");
        } if(foto.getUrl() == null || foto.getUrl().isBlank()) {
            throw new BusinessException("A url da foto é obrigatória.");
        }

        //Valida se o usuario existe
        Usuario usuario = usuarioRepository.findById(foto.getUsuario().getId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + foto.getUsuario().getId()));

        //Valida se o ponto turístico existe
        PontoTuristico ponto = pontoTuristicoRepository.findById(foto.getPontoTuristico().getId())
                .orElseThrow(() -> new BusinessException("Ponto turístico não encontrado com id: " + foto.getPontoTuristico().getId()));

        //dataUpload preenchida automaticamente
        foto.setDataUpload(LocalDate.now());

        // aprovado começa como false — aguarda aprovação do admin
        foto.setAprovado(false);

        foto.setUsuario(usuario);
        foto.setPontoTuristico(ponto);

        log.info("Salvando nova foto para o ponto: {}", ponto.getNome());
        return fotoRepository.save(foto);
    }

    // Admin aprova a foto para aparecer na galeria
    @Transactional
    @Override
    public Foto aprovar(Long id) {
        Foto foto = fotoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Foto não encontrada com id: " + id));

        if(foto.getAprovado()) {
            throw new BusinessException("Foto já está aprovada.");
        }

        foto.setAprovado(true);

        // NOVO — notifica o autor da foto que ela foi aprovada (RN24)
        notificacaoIService.criar(
                foto.getUsuario(),
                "Sua foto foi aprovada!",
                TipoNotificacao.FOTO_APROVADA
        );

        log.info("Aprovando foto id: {}", id);
        return fotoRepository.save(foto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!fotoRepository.existsById(id)) {
            throw new BusinessException("Foto não encontrada com id: " + id);
        }
        log.info("Removendo foto id: {}", id);
        fotoRepository.deleteById(id);
    }

    @Override
    public List<Foto> findByPontoTuristicoAndAprovado(Long idPonto, Boolean aprovado) {
        PontoTuristico ponto = pontoTuristicoRepository.findById(idPonto)
                .orElseThrow(() -> new BusinessException("Ponto não encontrado com id: " + idPonto));
        return fotoRepository.findByPontoTuristicoAndAprovado(ponto, aprovado);
    }

}