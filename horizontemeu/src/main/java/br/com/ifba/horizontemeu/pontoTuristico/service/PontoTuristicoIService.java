package br.com.ifba.horizontemeu.pontoTuristico.service;

import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PontoTuristicoIService {
    Page<PontoTuristico> findAll(Pageable pageable);
    Optional<PontoTuristico> findById(Long id);
    PontoTuristico save(PontoTuristico ponto);
    PontoTuristico update(Long id,   PontoTuristico pontoUpdate);
    void delete(long id);
    List<PontoTuristico> findByNome(String nome);

}
