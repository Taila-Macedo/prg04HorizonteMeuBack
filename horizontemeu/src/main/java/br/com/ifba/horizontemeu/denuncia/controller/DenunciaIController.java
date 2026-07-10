package br.com.ifba.horizontemeu.denuncia.controller;

import br.com.ifba.horizontemeu.denuncia.dto.DenunciaPostRequestDto;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DenunciaIController {
    ResponseEntity<?> findAll(Pageable pageable);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByUsuario(Long idUsuario);
    ResponseEntity<?> findByStatus(StatusDenuncia status, Pageable pageable);
    ResponseEntity<?> enviar(DenunciaPostRequestDto dto);
    ResponseEntity<?> resolver(Long id);
    ResponseEntity<?> rejeitar(Long id);
    ResponseEntity<?> resolverExcluindoConteudo(Long id);
    ResponseEntity<?> delete(Long id);
}