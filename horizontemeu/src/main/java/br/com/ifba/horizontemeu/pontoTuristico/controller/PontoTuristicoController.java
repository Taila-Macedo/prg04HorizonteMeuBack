package br.com.ifba.horizontemeu.pontoTuristico.controller;

import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoGetResponseDto;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPostRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPutRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.service.PontoTuristicoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pontos")
@RequiredArgsConstructor
public class PontoTuristicoController implements PontoTuristicoIController {

    private final PontoTuristicoIService pontoTuristicoIService;
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os pontos turísticos paginados.
     * GET /pontos?page=0&size=10
     * Requer: público (qualquer um pode ver os pontos)
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(Pageable pageable) {
        return ResponseEntity.ok(
                pontoTuristicoIService.findAll(pageable)
                        .map(p -> objectMapperUtil.map(p, PontoTuristicoGetResponseDto.class)));
    }

    /**
     * Busca ponto turístico por ID.
     * GET /pontos/{id}
     * Requer: público
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return pontoTuristicoIService.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        objectMapperUtil.map(p, PontoTuristicoGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ponto turístico não encontrado com id: " + id));
    }

    /**
     * Busca pontos pelo nome.
     * GET /pontos/buscar?nome=Torre
     * Requer: público
     */
    @Override
    @GetMapping(path = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                pontoTuristicoIService.findByNome(nome),
                PontoTuristicoGetResponseDto.class));
    }

    /**
     * Cadastra um novo ponto turístico.
     * POST /pontos
     * Requer: ADMINISTRADOR — protegido no SecurityConfig
     * notaMedia é iniciada em 0.0 pelo service automaticamente (RN04)
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid PontoTuristicoPostRequestDto dto) {
        PontoTuristico salvo = pontoTuristicoIService.save(
                objectMapperUtil.map(dto, PontoTuristico.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, PontoTuristicoGetResponseDto.class));
    }

    /**
     * Atualiza um ponto turístico.
     * PUT /pontos/{id}
     * Requer: ADMINISTRADOR — protegido no SecurityConfig
     * notaMedia não é alterada aqui — recalculada automaticamente (RN04)
     */
    @Override
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid PontoTuristicoPutRequestDto dto) {
        // Passa o DTO diretamente para o service
        // para garantir que notaMedia não seja sobrescrita
        PontoTuristico atualizado = pontoTuristicoIService.update(id, dto);
        return ResponseEntity.ok(
                objectMapperUtil.map(atualizado, PontoTuristicoGetResponseDto.class));
    }

    /**
     * Remove um ponto turístico pelo ID.
     * DELETE /pontos/{id}
     * Requer: ADMINISTRADOR — protegido no SecurityConfig
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        pontoTuristicoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}