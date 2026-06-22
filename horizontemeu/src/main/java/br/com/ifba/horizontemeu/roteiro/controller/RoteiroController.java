package br.com.ifba.horizontemeu.roteiro.controller;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPutRequestDto;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
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

    /**
     * Lista todos os roteiros de um usuário.
     * GET /roteiros/usuario/{idUsuario}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDtoList(
                        roteiroIService.findByUsuario(idUsuario)));
    }

    /**
     * Busca roteiro por ID.
     * GET /roteiros/{id}
     * Requer: público se publico=true, autenticado se publico=false (RN16)
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.findById(id)));
    }

    /**
     * Cria um novo roteiro.
     * POST /roteiros
     * Requer: autenticação
     * dataCriacao é preenchida automaticamente pelo service
     * Pontos são opcionais — roteiro pode ser criado vazio
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid RoteiroPostRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roteiroMapper.toGetResponseDto(roteiroIService.save(dto)));
    }

    /**
     * Atualiza título, descrição, data e lista de pontos do roteiro.
     * PUT /roteiros/{id}
     * Requer: autenticação
     * A lista de pontos é substituída por completo (não é incremental)
     */
    @Override
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid RoteiroPutRequestDto dto) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.update(id, dto)));
    }

    /**
     * Remove um roteiro e todos os seus pontos (cascade).
     * DELETE /roteiros/{id}
     * Requer: autenticação — RN13: só o dono pode deletar
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roteiroIService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca ou desmarca um ponto do roteiro como visitado (checklist).
     * PATCH /roteiros/pontos/{idRoteiroPonto}?visitado=true
     * Requer: autenticação
     * O campo visitado é independente por roteiro (RN12)
     */
    @Override
    @PatchMapping(path = "/pontos/{idRoteiroPonto}")
    public ResponseEntity<?> marcarComoVisitado(
            @PathVariable Long idRoteiroPonto,
            @RequestParam Boolean visitado) {
        roteiroIService.marcarComoVisitado(idRoteiroPonto, visitado);
        return ResponseEntity.ok("Status de visita atualizado com sucesso!");
    }

    /**
     * Torna o roteiro público — acessível por link sem autenticação (RN16).
     * PATCH /roteiros/{id}/compartilhar
     * Requer: autenticação — só o dono pode compartilhar
     */
    @Override
    @PatchMapping(path = "/{id}/compartilhar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> compartilhar(@PathVariable Long id) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.compartilhar(id)));
    }
}