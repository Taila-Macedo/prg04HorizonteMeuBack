package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.http.ResponseEntity;

public interface UsuarioIController {
    ResponseEntity<?> findAll();
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByNome(String nome);
    ResponseEntity<?> save(Usuario usuario);
    ResponseEntity<?> update(Long id, Usuario usuario);
    ResponseEntity<?> delete(Long id);
}