package br.com.ifba.horizontemeu.foto.controller;

import br.com.ifba.horizontemeu.foto.dto.FotoGetResponseDto;
import br.com.ifba.horizontemeu.foto.dto.FotoPostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface FotoIController {
    ResponseEntity<Page<FotoGetResponseDto>> findAll(Pageable pageable);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByPontoTuristico(Long idPonto);
    ResponseEntity<?> findByAprovado(Boolean aprovado);
    ResponseEntity<?> save(FotoPostRequestDto dto);
    ResponseEntity<?> aprovar(Long id);
    ResponseEntity<?> delete(Long id);
}