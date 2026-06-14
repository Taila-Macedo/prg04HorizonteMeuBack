package br.com.ifba.horizontemeu.favorito.service;

import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import br.com.ifba.horizontemeu.favorito.repository.FavoritoRepository;
import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.repository.PontoTuristicoRepository;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService  implements FavoritoIService{

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;

    private static final Logger log = LoggerFactory.getLogger(FavoritoService.class);

    @Override
    public List<Favorito> findByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + idUsuario));

        log.info("Buscando favoritos do usuário id: {}", idUsuario);
        return favoritoRepository.findByUsuario(usuario);
    }

    @Transactional
    @Override
    public Favorito save(Favorito favorito) {
        if (favorito == null) {
            throw new BusinessException("Dados do favorito não preenchidos.");
        }

        //Valida se o usuario existe
        Usuario usuario = usuarioRepository.findById(favorito.getUsuario().getId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + favorito.getUsuario().getId()));

        //Valida se o ponto turístico existe
        PontoTuristico ponto = pontoTuristicoRepository.findById(favorito.getPontoTuristico().getId())
                .orElseThrow(() -> new BusinessException("Ponto turístico não encontrado com id: " + favorito.getPontoTuristico().getId()));

        // Um usuário não pode favoritar o mesmo ponto duas vezes
        if (favoritoRepository.findByUsuarioAndPontoTuristico(usuario, ponto).isPresent()) {
            throw new BusinessException("Este ponto turístico já está nos favoritos do usuário.");
        }

        //Preenche automaticamente a data de quando foi salvo
        favorito.setDataSalvo(LocalDate.now());

        //Associa as entidades completas(com os dados) ao favorito
        favorito.setUsuario(usuario);
        favorito.setPontoTuristico(ponto);

        log.info("Salvando favorito do usuário {} para o ponot {}", usuario.getEmail(), ponto.getNome());
        return favoritoRepository.save(favorito);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!favoritoRepository.existsById(id)) {
            throw new BusinessException("Favorito não encontrado com id: " + id);
        }

        log.info("Removendo favorito id: {}", id);
        favoritoRepository.deleteById(id);
    }
}
