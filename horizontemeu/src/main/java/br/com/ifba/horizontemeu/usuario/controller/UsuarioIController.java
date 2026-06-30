package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.usuario.dto.AlterarSenhaRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPostRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPutRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioIController {
    ResponseEntity<?> findAll(Pageable pageble);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByNome(String nome);
    ResponseEntity<?> save(UsuarioPostRequestDto usuarioPostRequestDto);
    ResponseEntity<?> update(Long id, UsuarioPutRequestDto usuarioPutRequestDto);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> alterarSenha(Long id, AlterarSenhaRequestDto dto);
}