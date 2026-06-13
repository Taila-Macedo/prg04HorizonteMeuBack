package br.com.ifba.horizontemeu.pontoTuristico.controller;

import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoGetResponseDto;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPostRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.service.PontoTuristicoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping(path = "/pontos")
@RequiredArgsConstructor
public class PontoTuristicoController implements PontoTuristicoIController{

    private final PontoTuristicoIService pontoTuristicoIService;
    // coloca o ObjectMapperUtil para converter entidades em DTOs e vice-versa
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os pontos turísticos
     * GET /pontos/findall
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.pontoTuristicoIService.findAll(pageable)
                        .map(p -> objectMapperUtil.map(p, PontoTuristicoGetResponseDto.class)));
    }

    /**
     * Busca usuário por ID.
     * GET /pontos/findbyid/{id}
     * Converte o Usuario encontrado para DTO antes de retornar
     * se não encontrar retorna 404
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return pontoTuristicoIService.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        objectMapperUtil.map(p, PontoTuristicoGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ponto turístico não encontrado com id: " + id));
    }

    /**
     * Busca pontos turísticos pelo nome.
     * GET /pontos/findbynome?nome=Torre
     */
    @Override
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                pontoTuristicoIService.findByNome(nome),
                PontoTuristicoGetResponseDto.class));
    }

    /**
     * Cadastra um novo ponto turístico.
     * POST /pontos/save
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid PontoTuristicoPostRequestDto pontoTuristicoPostRequestDto) {
        PontoTuristico salvo = pontoTuristicoIService.save(
                objectMapperUtil.map(pontoTuristicoPostRequestDto, PontoTuristico.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, PontoTuristicoGetResponseDto.class));
    }

    /**
     * Atualiza um ponto turístico.
     * PUT /pontos/update/{id}
     */
    @Override
    @PutMapping(path = "/update/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid PontoTuristicoPostRequestDto pontoTuristicoPostRequestDto) {
        PontoTuristico atualizado = pontoTuristicoIService.update(id,
                objectMapperUtil.map(pontoTuristicoPostRequestDto, PontoTuristico.class));
        return ResponseEntity.ok(
                objectMapperUtil.map(atualizado, PontoTuristicoGetResponseDto.class));
    }

    /**
     * Remove um ponto turístico pelo ID.
     * DELETE /pontos/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        pontoTuristicoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
