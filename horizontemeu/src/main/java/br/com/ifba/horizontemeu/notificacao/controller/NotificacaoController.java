package br.com.ifba.horizontemeu.notificacao.controller;

import br.com.ifba.horizontemeu.notificacao.mapper.NotificacaoMapper;
import br.com.ifba.horizontemeu.notificacao.service.NotificacaoIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notificacoes")
public class NotificacaoController implements NotificacaoIController {

    private final NotificacaoIService notificacaoIService;
    private final NotificacaoMapper notificacaoMapper;

    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacaoMapper.toGetResponseDtoList(
                notificacaoIService.findByUsuario(idUsuario)
        ));
    }

    @Override
    @PatchMapping(path = "/{id}/lida")
    public ResponseEntity<?> marcarComoLida(@PathVariable Long id) {
        notificacaoIService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        notificacaoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}