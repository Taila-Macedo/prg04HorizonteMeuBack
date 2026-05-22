package br.com.ifba.horizontemeu.usuario.controller;

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

    /**
     * Lista todos os usuários.
     * GET /usuarios/findall
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(usuarioIService.findAll());
    }

    /**
     * Busca usuário por ID.
     * GET /usuarios/findbyid/{id}
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return usuarioIService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
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
        return ResponseEntity.ok(usuarioIService.findByNome(nome));
    }

    /**
     * Cadastra um novo usuário.
     * POST /usuarios/save
     * Body:
     * {
     *   "nome": "Ana Silva",
     *   "email": "ana@email.com",
     *   "senha": "123456"
     * }
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioIService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Atualiza nome e foto de perfil.
     * PUT /usuarios/update/{id}
     * Body:
     * {
     *   "nome": "Ana Silva Santos",
     *   "fotoPerfil": "https://exemplo.com/foto.jpg"
     * }
     */
    @Override
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario atualizado = usuarioIService.update(id, usuario);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Remove um usuário pelo ID.
     * DELETE /usuarios/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            usuarioIService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}