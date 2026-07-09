package br.com.ifba.horizontemeu.notificacao.controller;

import org.springframework.http.ResponseEntity;

public interface NotificacaoIController {
    ResponseEntity<?> findByUsuario(Long idUsuario);
    ResponseEntity<?> marcarComoLida(Long id);
    ResponseEntity<?> delete(Long id);
}
