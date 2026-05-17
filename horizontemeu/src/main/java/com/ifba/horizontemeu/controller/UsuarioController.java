package com.ifba.horizontemeu.controller;

import com.ifba.horizontemeu.entity.Usuario;
import com.ifba.horizontemeu.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping(path = "/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista todos os usuários.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }

    /**
     * Busca usuário por ID.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Busca usuários pelo nome.
     */
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByNome(@RequestParam String nome) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.findByNome(nome));
    }

    /**
     * Cadastra um novo usuário.
     * Body:
     * {
     *   "nome": "Ana Silva",
     *   "email": "ana@email.com",
     *   "senha": "123456"
     * }
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Atualiza nome e foto de perfil.
     * Body:
     * {
     *   "nome": "Ana Silva Santos",
     *   "fotoPerfil": "https://exemplo.com/foto.jpg"
     * }
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(usuarioService.update(id, usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Remove um usuário pelo ID.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
