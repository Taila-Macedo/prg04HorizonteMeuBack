package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioGetResponseDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPostRequestDto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.service.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioIController {

    private final UsuarioIService usuarioIService;
    // coloca o ObjectMapperUtil para converter entidades em DTOs e vice-versa
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os usuários.
     * GET /usuarios/findall
     * Usa mapAll para converter a lista de Usuario em lista de UsuarioGetResponseDto
     * garantindo que a senha não seja exposta na resposta
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(objectMapperUtil.mapAll(
                        this.usuarioIService.findAll(),
                        UsuarioGetResponseDto.class));
    }

    /**
     * Busca usuário por ID.
     * GET /usuarios/findbyid/{id}
     * Converte o Usuario encontrado para DTO antes de retornar
     * se não encontrar retorna 404
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return usuarioIService.findById(id)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        objectMapperUtil.map(u, UsuarioGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuário não encontrado com id: " + id));
    }

    /**
     * Busca usuários pelo nome.
     * GET /usuarios/findbynome?nome=Ana
     */
    @Override
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                usuarioIService.findByNome(nome),
                UsuarioGetResponseDto.class));
    }

    /**
     * Cadastra um novo usuário.
     * POST /usuarios/save
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody UsuarioPostRequestDto usuarioPostRequestDto) {
        // Converte o DTO de entrada para a entidade Usuario
        Usuario salvo = usuarioIService.save(
                objectMapperUtil.map(usuarioPostRequestDto, Usuario.class));
        // Converte o Usuario salvo para DTO de saída e retorna 201
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, UsuarioGetResponseDto.class));
    }

    /**
     * Atualiza nome e foto de perfil.
     * PUT /usuarios/update/{id}
     */
    @Override
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioPostRequestDto usuarioPostRequestDto) {
        // Converte o DTO de entrada para entidade e atualiza
        Usuario atualizado = usuarioIService.update(id,
                objectMapperUtil.map(usuarioPostRequestDto, Usuario.class));
        // Retorna o usuário atualizado como DTO de saída
        return ResponseEntity.ok(
                objectMapperUtil.map(atualizado, UsuarioGetResponseDto.class));
    }

    /**
     * Remove um usuário pelo ID.
     * DELETE /usuarios/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        // O ApiExceptionHandler cuida do erro caso o usuário não exista
        usuarioIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}