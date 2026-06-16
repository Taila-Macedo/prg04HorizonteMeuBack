package br.com.ifba.horizontemeu.roteiro.controller;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import org.springframework.http.ResponseEntity;

public interface RoteiroIController {
    ResponseEntity<?> findByUsuario(Long idUsuario);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> save(RoteiroPostRequestDto dto);
    ResponseEntity<?> update(Long id, RoteiroPostRequestDto dto);
    ResponseEntity<Void> delete(Long id);
    ResponseEntity<?> marcarComoVisitado(Long idRoteiroPonto, Boolean visitado);
}