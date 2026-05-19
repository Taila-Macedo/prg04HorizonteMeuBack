package com.ifba.horizontemeu.controller;

import com.ifba.horizontemeu.entity.Usuario;
import com.ifba.horizontemeu.service.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping(path = "/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioIController {

    private final UsuarioIService usuarioIService;

    /**
     * Lista todos os usuários.
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Usuario> findAll() {
        return usuarioIService.findAll();
    }

    /**
     * Busca usuário por ID.
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Usuario findById(@PathVariable Long id) {
        return usuarioIService.findById(id).orElse(null);
    }

    /**
     * Busca usuários pelo nome.
     */
    @Override
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Usuario> findByNome(@RequestParam String nome) {
        return usuarioIService.findByNome(nome);
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
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario save(@RequestBody Usuario usuario) {
        return usuarioIService.save(usuario);
    }

    /**
     * Atualiza nome e foto de perfil.
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
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioIService.update(id, usuario);
    }

    /**
     * Remove um usuário pelo ID.
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public void delete(@PathVariable Long id) {
        usuarioIService.delete(id);
    }

}