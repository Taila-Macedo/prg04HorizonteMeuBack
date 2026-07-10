package br.com.ifba.horizontemeu.comentario.controller;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioGetResponseDto;
import br.com.ifba.horizontemeu.comentario.dto.ComentarioPostRequestDto;
import br.com.ifba.horizontemeu.comentario.dto.ComentarioPutRequestDto;
import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.comentario.mapper.ComentarioMapper;
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

    //ComentarioMapper para conversões com campos aninhados
    private final ComentarioMapper comentarioMapper;

    /**
     * Lista todos os comentários paginados.
     * GET /comentarios?page=0&size=10
     * Requer: autenticação
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ComentarioGetResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(comentarioIService.findAll(pageable)
                        .map(c -> comentarioMapper.toDto(c)));
    }

    /**
     * Busca comentário por ID.
     * GET /comentarios/{id}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return comentarioIService.findById(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(comentarioMapper.toDto(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comentário não encontrado com id: " + id));
    }

    /**
     * Busca todos os comentários de um ponto turístico.
     * GET /comentarios/ponto/{idPonto}
     * Requer: público — comentários são visíveis para todos
     */
    @Override
    @GetMapping(path = "/ponto/{idPonto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPontoTuristico(@PathVariable Long idPonto) {
        return ResponseEntity.ok(
                comentarioMapper.toDtoList(
                        comentarioIService.findByPontoTuristico(idPonto)));
    }

    /**
     * Busca todos os comentários de um usuário.
     * GET /comentarios/usuario/{idUsuario}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                comentarioMapper.toDtoList(
                        comentarioIService.findByUsuario(idUsuario)));
    }

    /**
     * Publica um novo comentário em um ponto turístico.
     * POST /comentarios
     * Requer: autenticação
     * - curtidas começa em 0 automaticamente
     * - editado começa como false automaticamente
     * - data é preenchida automaticamente pelo service
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid ComentarioPostRequestDto dto) {
        Comentario comentario = new Comentario();
        comentario.setTexto(dto.getTexto());
        comentario.setNota(dto.getNota());
        comentario.setFotoUrl(dto.getFotoUrl());

        // Cria objetos com só o ID para o service buscar e validar no banco
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        comentario.setUsuario(usuario);

        PontoTuristico ponto = new PontoTuristico();
        ponto.setId(dto.getIdPontoTuristico());
        comentario.setPontoTuristico(ponto);

        Comentario salvo = comentarioIService.save(comentario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioMapper.toDto(salvo));
    }

    /**
     * Atualiza texto e/ou foto de um comentário.
     * PUT /comentarios/{id}
     * Requer: autenticação — RN13: só o dono ou admin pode editar
     * Nota é imutável — não pode ser alterada após publicação
     */
    @Override
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid ComentarioPutRequestDto dto) {

         // Não existe um ComentarioMapper.toEntity() pronto pro DTO de update,
        // então montei a entidade manualmente aqui, só com os campos editáveis
        Comentario comentario = new Comentario();
        comentario.setTexto(dto.getTexto());
        comentario.setFotoUrl(dto.getFotoUrl());

        // service garante que só texto e fotoUrl mudam (nota é imutável)
        Comentario atualizado = comentarioIService.update(id, comentario);
        return ResponseEntity.ok(comentarioMapper.toDto(atualizado));
    }

    /**
     * Incrementa o contador de curtidas do comentário em 1.
     * PATCH /comentarios/{id}/curtir
     * Requer: autenticação
     * Atenção: RN21 — curtida única por usuário não está implementada ainda
     * (precisa de tabela auxiliar CurtidaComentario)
     */
    @Override
    @PatchMapping(path = "/{id}/curtir", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> curtir(@PathVariable Long id) {
        return ResponseEntity.ok(
                comentarioMapper.toDto(comentarioIService.curtir(id)));
    }

    /**
     * Remove um comentário pelo ID.
     * DELETE /comentarios/{id}
     * Requer: autenticação — RN13: só o dono ou admin pode deletar
     * Após deletar, nota_media do ponto é recalculada automaticamente (RN04)
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        comentarioIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
