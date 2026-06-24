package br.com.ifba.horizontemeu.pontoTuristico.controller;

import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoGetResponseDto;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPostRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPutRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

public interface PontoTuristicoIController {
    ResponseEntity<?> findAll(Pageable pageble);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByNome(String nome);
    ResponseEntity<?>  save(PontoTuristicoPostRequestDto pontoTuristicoPostRequestDto);
    ResponseEntity<?> update(Long id, PontoTuristicoPutRequestDto dto);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> findByPais(String pais);
}
