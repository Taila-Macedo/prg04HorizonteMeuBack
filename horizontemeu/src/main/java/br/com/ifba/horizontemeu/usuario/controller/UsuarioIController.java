package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.usuario.dto.UsuarioPostRequestDto;
import org.springframework.http.ResponseEntity;

public interface UsuarioIController {
    ResponseEntity<?> findAll();
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByNome(String nome);
    ResponseEntity<?> save(UsuarioPostRequestDto usuarioPostRequestDto);
    ResponseEntity<?> update(Long id, UsuarioPostRequestDto usuarioPostRequestDto);
    ResponseEntity<?> delete(Long id);
}