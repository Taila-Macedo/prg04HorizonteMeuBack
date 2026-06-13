package br.com.ifba.horizontemeu.foto.service;

import br.com.ifba.horizontemeu.foto.entity.Foto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FotoIService {
    Page<Foto> findAll(Pageable pageable);
    Optional<Foto> findById(Long id);
    List<Foto> findByPontoTuristico(Long idPonto);
    List<Foto> findByAprovado(Boolean aprovado);
    Foto save(Foto foto);
    Foto aprovar(Long id);
    void delete(Long id);
}
