package br.com.ifba.horizontemeu.comentario.controller;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioGetResponseDto;
import br.com.ifba.horizontemeu.comentario.dto.ComentarioPostRequestDto;
import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.comentario.service.ComentarioIService;
import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/comentarios")
@RequiredArgsConstructor
public class ComentarioController implements ComentarioIController {

    private final ComentarioIService comentarioIService;
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os comentários paginados.
     * GET /comentarios/findall
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ComentarioGetResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(comentarioIService.findAll(pageable)
                        .map(c -> objectMapperUtil.map(c, ComentarioGetResponseDto.class)));
    }

    /**
     * Busca comentário por ID.
     * GET /comentarios/findbyid/{id}
     * Se não encontrar retorna 404
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return comentarioIService.findById(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(
                        objectMapperUtil.map(c, ComentarioGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comentário não encontrado com id: " + id));
    }

    /**
     * Busca todos os comentários de um ponto turístico.
     * GET /comentarios/findbyponto/{idPonto}
     */
    @Override
    @GetMapping(path = "/findbyponto/{idPonto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPontoTuristico(@PathVariable Long idPonto) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                comentarioIService.findByPontoTuristico(idPonto),
                ComentarioGetResponseDto.class));
    }

    /**
     * Busca todos os comentários de um usuário.
     * GET /comentarios/findbyusuario/{idUsuario}
     */
    @Override
    @GetMapping(path = "/findbyusuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                comentarioIService.findByUsuario(idUsuario),
                ComentarioGetResponseDto.class));
    }

    /**
     * Publica um novo comentário.
     * POST /comentarios/save
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid ComentarioPostRequestDto dto) {
        Comentario comentario = new Comentario();
        comentario.setTexto(dto.getTexto());
        comentario.setNota(dto.getNota());
        comentario.setFotoUrl(dto.getFotoUrl());

        // cria objetos com só o ID para o Service buscar e validar
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        comentario.setUsuario(usuario);

        PontoTuristico ponto = new PontoTuristico();
        ponto.setId(dto.getIdPontoTuristico());
        comentario.setPontoTuristico(ponto);

        Comentario salvo = comentarioIService.save(comentario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, ComentarioGetResponseDto.class));
    }

    /**
     * Atualiza o texto e/ou foto de um comentário.
     * PUT /comentarios/update/{id}
     */
    @Override
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ComentarioPostRequestDto dto) {
        Comentario comentarioUpdate = new Comentario();
        comentarioUpdate.setTexto(dto.getTexto());
        comentarioUpdate.setFotoUrl(dto.getFotoUrl());

        Comentario atualizado = comentarioIService.update(id, comentarioUpdate);
        return ResponseEntity.ok(
                objectMapperUtil.map(atualizado, ComentarioGetResponseDto.class));
    }

    /**
     * Incrementa o contador de curtidas do comentário em 1.
     * PATCH /comentarios/curtir/{id}
     */
    @Override
    @PatchMapping(path = "/curtir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> curtir(@PathVariable Long id) {
        Comentario curtido = comentarioIService.curtir(id);
        return ResponseEntity.ok(
                objectMapperUtil.map(curtido, ComentarioGetResponseDto.class));
    }

    /**
     * Remove um comentário pelo ID.
     * DELETE /comentarios/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        comentarioIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
