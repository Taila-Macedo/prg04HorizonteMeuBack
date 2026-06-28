package br.com.ifba.horizontemeu.foto.controller;

import br.com.ifba.horizontemeu.foto.dto.FotoGetResponseDto;
import br.com.ifba.horizontemeu.foto.dto.FotoPostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FotoIController {
    ResponseEntity<Page<FotoGetResponseDto>> findAll(Pageable pageable);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findByPontoTuristico(Long idPonto);
    ResponseEntity<?> findByAprovado(Boolean aprovado);
    ResponseEntity<?> save(MultipartFile arquivo, Long idUsuario, Long idPontoTuristico, String legenda);
    ResponseEntity<?> aprovar(Long id);
    ResponseEntity<?> delete(Long id);
}