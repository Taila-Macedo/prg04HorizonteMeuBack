package br.com.ifba.horizontemeu.comentario.service;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioPutRequestDto;
import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ComentarioIService {

    Page<Comentario> findAll(Pageable pageable);
    Optional<Comentario> findById(Long id);
    List<Comentario> findByPontoTuristico(Long idPonto);
    List<Comentario> findByUsuario(Long idUsuario);
    Comentario save(Comentario comentario);
    Comentario update(Long id, Comentario comentario);
    Comentario curtir(Long id, Long idUsuario);
    void delete(Long id);
}