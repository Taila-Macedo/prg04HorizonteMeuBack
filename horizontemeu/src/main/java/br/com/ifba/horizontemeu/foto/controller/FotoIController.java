package br.com.ifba.horizontemeu.foto.controller;

import br.com.ifba.horizontemeu.foto.dto.FotoPostRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


public interface FotoIController {
    ResponseEntity<?> findAll(Pageable pageable);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByPontoTuristico(Long idPonto);
    ResponseEntity<?> findByAprovado(Boolean aprovado);
    ResponseEntity<?> save(FotoPostRequestDto fotoPostRequestDto);
    ResponseEntity<?> aprovar(Long id);
    ResponseEntity<?> delete(Long id);
}
