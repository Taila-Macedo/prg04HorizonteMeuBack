package br.com.ifba.horizontemeu.pontoTuristico.service;

import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPutRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PontoTuristicoIService {
    Page<PontoTuristico> findAll(Pageable pageable);
    Optional<PontoTuristico> findById(Long id);
    PontoTuristico save(PontoTuristico ponto);
    // ALTERADO: antes era update(Long id, PontoTuristicoPutRequestDto dto)
    PontoTuristico update(Long id, PontoTuristico ponto);
    void delete(long id);
    List<PontoTuristico> findByNome(String nome);
    List<PontoTuristico> findByPais(String pais);
}