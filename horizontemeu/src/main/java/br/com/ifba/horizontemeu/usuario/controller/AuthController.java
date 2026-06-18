package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.usuario.dto.LoginRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.LoginResponseDto;
import br.com.ifba.horizontemeu.usuario.service.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de autenticação — separado do UsuarioController
 * porque login é uma operação pública e independente do CRUD de usuário.
 *
 * Rota base: /auth
 * Não precisa de token JWT para acessar (configurado como público no SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioIService usuarioIService;

    /**
     * Realiza o login do usuário.
     * POST /auth/login
     *
     * Body esperado:
     * {
     *   "email": "usuario@email.com",
     *   "senha": "minhasenha"
     * }
     *
     * Resposta (200 OK):
     * {
     *   "token": "eyJ...",
     *   "id": 1,
     *   "nome": "Taila",
     *   "email": "usuario@email.com",
     *   "perfil": "USUARIO"
     * }
     *
     * O token deve ser enviado em todas as próximas requisições no cabeçalho:
     *   Authorization: Bearer eyJ...
     */
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        LoginResponseDto response = usuarioIService.login(dto);
        return ResponseEntity.ok(response);
    }
}