package br.com.ifba.horizontemeu.roteiro.controller;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPutRequestDto;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro; // ALTERADO: import adicionado (faltava)
import br.com.ifba.horizontemeu.roteiro.mapper.RoteiroMapper;
import br.com.ifba.horizontemeu.roteiro.service.RoteiroIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roteiros")
@RequiredArgsConstructor
public class RoteiroController implements RoteiroIController {

    private final RoteiroIService roteiroIService;
    private final RoteiroMapper roteiroMapper;

    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDtoList(
                        roteiroIService.findByUsuario(idUsuario)));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.findById(id)));
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid RoteiroPostRequestDto dto) {
        // ALTERADO: agora o toEntity() acontece aqui no controller (antes era dentro do service)
        Roteiro roteiro = roteiroMapper.toEntity(dto);
        // ALTERADO: passa a entidade + idUsuario + pontos separados pro service
        Roteiro salvo = roteiroIService.save(roteiro, dto.getIdUsuario(), dto.getPontos());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roteiroMapper.toGetResponseDto(salvo));
    }

    @Override
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid RoteiroPutRequestDto dto) {
        // ALTERADO: mesma lógica do save — toEntity() migrou pra cá
        Roteiro roteiro = roteiroMapper.toEntity(dto);
        Roteiro atualizado = roteiroIService.update(id, roteiro, dto.getPontos());
        return ResponseEntity.ok(roteiroMapper.toGetResponseDto(atualizado));
    }

    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roteiroIService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/pontos/{idRoteiroPonto}")
    public ResponseEntity<?> marcarComoVisitado(
            @PathVariable Long idRoteiroPonto,
            @RequestParam Boolean visitado) {
        roteiroIService.marcarComoVisitado(idRoteiroPonto, visitado);
        return ResponseEntity.ok("Status de visita atualizado com sucesso!");
    }

    @Override
    @PatchMapping(path = "/{id}/compartilhar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> compartilhar(@PathVariable Long id) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.compartilhar(id)));
    }
}