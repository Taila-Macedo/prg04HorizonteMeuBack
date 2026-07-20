package br.com.ifba.horizontemeu.comentario.service;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioPutRequestDto;
import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.comentario.repository.ComentarioRepository;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import br.com.ifba.horizontemeu.favorito.repository.FavoritoRepository;
import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComentarioService implements ComentarioIService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;
    // NOVO — usado para notificar quem favoritou o ponto (RN14) e o autor do comentário (curtida)
    private final NotificacaoIService notificacaoIService;
    private final FavoritoRepository favoritoRepository;

    private static final Logger log = LoggerFactory.getLogger(ComentarioService.class);

    @Override
    public Page<Comentario> findAll(Pageable pageable) {
        log.info("Buscando todos os comentários...");
        return comentarioRepository.findAll(pageable);
    }

    @Override
    public Optional<Comentario> findById(Long id) {
        log.info("Buscando comentário com id: {}", id);
        return comentarioRepository.findById(id);
    }

    @Override
    public List<Comentario> findByPontoTuristico(Long idPonto) {
        log.info("Buscando comentários do ponto turístico id: {}", idPonto);

        //Retorna lista vazia se o ponto não existir
        return pontoTuristicoRepository.findById(idPonto)
                .map(comentarioRepository::findByPontoTuristico)
                .orElse(List.of());
    }

    @Override
    public List<Comentario> findByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + idUsuario));

        log.info("Buscando comentários do usuário id: {}", idUsuario);
        return comentarioRepository.findByUsuario(usuario);
    }

    @Transactional
    @Override
    public Comentario save(Comentario comentario) {
        // Validações básicas
        if (comentario == null) {
            throw new BusinessException("Dados do comentário não preenchidos.");
        }
        if (comentario.getId() != null) {
            throw new BusinessException("Comentário já existente. Use o update.");
        }
        if (comentario.getTexto() == null || comentario.getTexto().isBlank()) {
            throw new BusinessException("O texto do comentário não pode ser vazio.");
        }
        // Nota precisa estar entre 1 e 5 — regra de negócio
        if (comentario.getNota() == null || comentario.getNota() < 1 || comentario.getNota() > 5) {
            throw new BusinessException("A nota deve ser um valor entre 1 e 5.");
        }

        // Confirma que o usuário  e o ponto turístico existe — busca o objeto completo no banco
        Usuario usuario = usuarioRepository.findById(comentario.getUsuario().getId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + comentario.getUsuario().getId()));

        PontoTuristico ponto = pontoTuristicoRepository.findById(comentario.getPontoTuristico().getId())
                .orElseThrow(() -> new BusinessException("Ponto turístico não encontrado com id: " + comentario.getPontoTuristico().getId()));

        // Preenche automaticamente os campos controlados pelo sistema
        comentario.setData(LocalDateTime.now()); // data/hora atual da publicação
        comentario.setCurtidas(0);               // todo comentário novo começa com 0 curtidas
        comentario.setEditado(false);            // ainda não foi editado

        // Associa as entidades completas (com todos os dados) ao comentário
        comentario.setUsuario(usuario);
        comentario.setPontoTuristico(ponto);

        log.info("Salvando novo comentário do usuário {} no ponto {}", usuario.getEmail(), ponto.getNome());
        Comentario salvo = comentarioRepository.save(comentario);

        // Após salvar, recalcula a nota_media do ponto turístico
        recalcularNotaMedia(ponto);

        // NOVO — notifica todo mundo que favoritou esse ponto sobre o comentário novo (RN14)
        notificarFavoritosSobreComentario(ponto);

        return salvo;
    }

    @Transactional
    @Override
    public Comentario update(Long id, Comentario comentario) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado com id: " + id));

        // ALTERADO: antes lia "dto.getTexto()" etc. — agora lê do objeto "comentario" (entidade) recebido
        existente.setTexto(comentario.getTexto());
        existente.setFotoUrl(comentario.getFotoUrl());

        existente.setEditado(true);

        log.info("Atualizando comentário id: {}", id);
        return comentarioRepository.save(existente);
    }

    // ALTERADO (RN21) — agora funciona como toggle e impede que o mesmo
    // usuário curta o mesmo comentário mais de uma vez. O controle é feito
    // pelo conjunto "usuariosQueCurtiram" dentro do próprio Comentario:
    // se o id do usuário já está lá, remove (descurtir); senão, adiciona
    // (curtir). Não foi criada nenhuma entidade nova para isso.
    @Transactional
    @Override
    public Comentario curtir(Long id, Long idUsuario) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado com id: " + id));

        boolean jaCurtiu = comentario.getUsuariosQueCurtiram().contains(idUsuario);

        if (jaCurtiu) {
            // Usuário já tinha curtido — descurtir
            comentario.getUsuariosQueCurtiram().remove(idUsuario);
            comentario.setCurtidas(Math.max(0, comentario.getCurtidas() - 1));
            log.info("Usuário {} removeu a curtida do comentário id: {} — total: {}",
                    idUsuario, id, comentario.getCurtidas());
        } else {
            // Usuário ainda não tinha curtido — registra a curtida
            comentario.getUsuariosQueCurtiram().add(idUsuario);
            comentario.setCurtidas(comentario.getCurtidas() + 1);

            // Notifica o autor do comentário, exceto quando ele curte o próprio comentário
            if (!comentario.getUsuario().getId().equals(idUsuario)) {
                notificacaoIService.criar(
                        comentario.getUsuario(),
                        "Seu comentário recebeu uma nova curtida!",
                        TipoNotificacao.CURTIDA
                );
            }
            log.info("Usuário {} curtiu o comentário id: {} — total: {}", idUsuario, id, comentario.getCurtidas());
        }

        return comentarioRepository.save(comentario);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado com id: " + id));

        PontoTuristico ponto = comentario.getPontoTuristico();

        log.info("Removendo comentário id: {}", id);
        comentarioRepository.deleteById(id);

        // Após remover, recalcula a nota_media do ponto turístico
        recalcularNotaMedia(ponto);
    }

    // Método auxiliar — recalcula a média das notas de todos os comentários do ponto
    private void recalcularNotaMedia(PontoTuristico ponto) {
        // Busca todos os comentários atuais daquele ponto
        List<Comentario> comentarios = comentarioRepository.findByPontoTuristico(ponto);

        if (comentarios.isEmpty()) {
            // Se não tem nenhum comentário, a média volta para 0
            ponto.setNotaMedia(0.0f);
        } else {
            // Soma todas as notas e divide pela quantidade de comentários
            double media = comentarios.stream()
                    .mapToInt(Comentario::getNota)
                    .average()
                    .orElse(0.0);
            ponto.setNotaMedia((float) media);
        }

        // Salva o ponto turístico com a nova nota_media
        pontoTuristicoRepository.save(ponto);
        log.info("Nota média do ponto {} recalculada para: {}", ponto.getNome(), ponto.getNotaMedia());
    }

    //Método auxiliar: busca quem favoritou o ponto e cria uma notificação pra cada um (RN14)
    private void notificarFavoritosSobreComentario(PontoTuristico ponto) {
        List<Favorito> favoritos = favoritoRepository.findByPontoTuristico(ponto);

        for (Favorito favorito : favoritos) {
            notificacaoIService.criar(
                    favorito.getUsuario(),
                    "Novo comentário em " + ponto.getNome(),
                    TipoNotificacao.COMENTARIO
            );
        }

        log.info("Notificados {} usuários que favoritaram o ponto {}", favoritos.size(), ponto.getNome());
    }
}