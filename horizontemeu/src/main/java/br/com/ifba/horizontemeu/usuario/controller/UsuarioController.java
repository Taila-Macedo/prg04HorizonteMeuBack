package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.usuario.dto.AlterarSenhaRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioGetResponseDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPostRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPutRequestDto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.service.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioIController {

    private final UsuarioIService usuarioIService;
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os usuários paginados.
     * GET /usuarios
     * Requer: autenticação (qualquer perfil)
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UsuarioGetResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(
                usuarioIService.findAll(pageable)
                        .map(u -> objectMapperUtil.map(u, UsuarioGetResponseDto.class))
        );
    }

    /**
     * Busca usuário por ID.
     * GET /usuarios/{id}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return usuarioIService.findById(id)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        objectMapperUtil.map(u, UsuarioGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuário não encontrado com id: " + id));
    }

    /**
     * Busca usuários pelo nome.
     * GET /usuarios/buscar?nome=...
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                usuarioIService.findByNome(nome),
                UsuarioGetResponseDto.class));
    }

    /**
     * Cadastra um novo usuário.
     * POST /usuarios
     * Requer: PÚBLICO (sem token) — é o fluxo de registro
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid UsuarioPostRequestDto dto) {
        // Converte o DTO para entidade — o service vai criptografar a senha e fixar o perfil
        Usuario salvo = usuarioIService.save(
                objectMapperUtil.map(dto, Usuario.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, UsuarioGetResponseDto.class));
    }

    /**
     * Atualiza nome e foto de perfil do usuário.
     * PUT /usuarios/update/{id}
     * Requer: autenticação
     *
     * Usa UsuarioPutRequestDto (só nome e fotoPerfil) — email e senha
     * têm fluxos próprios e não são alterados aqui.
     */
    @Override
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid UsuarioPutRequestDto dto) {
        // Passa o DTO diretamente para o service — não converte para entidade
        // para garantir que só nome e fotoPerfil sejam atualizados
        Usuario atualizado = usuarioIService.update(id, dto);
        return ResponseEntity.ok(
                objectMapperUtil.map(atualizado, UsuarioGetResponseDto.class));
    }



    /**
     * Remove um usuário pelo ID.
     * DELETE /usuarios/{id}
     * Requer: ADMINISTRADOR (protegido no SecurityConfig)
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        usuarioIService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Altera a senha do usuário autenticado.
     * PUT /usuarios/{id}/senha
     * Requer: autenticação
     */
    @PutMapping(path = "/{id}/senha",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> alterarSenha(@PathVariable Long id,
                                          @RequestBody @Valid AlterarSenhaRequestDto dto) {
        usuarioIService.alterarSenha(id, dto);
        return ResponseEntity.ok().body(java.util.Map.of("mensagem", "Senha alterada com sucesso."));
    }
}