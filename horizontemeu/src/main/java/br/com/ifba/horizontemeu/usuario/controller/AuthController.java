package br.com.ifba.horizontemeu.usuario.controller;

import br.com.ifba.horizontemeu.usuario.dto.LoginRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.LoginResponseDto;
import br.com.ifba.horizontemeu.usuario.dto.RedefinirSenhaRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.SolicitarCodigoRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.ValidarCodigoRequestDto;
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
 * Controller de autenticação.
 * Rota base: /auth
 * Todos os endpoints aqui são públicos (configurado no SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioIService usuarioIService;

    /**
     * POST /auth/login
     * Autentica o usuário e retorna o JWT.
     */
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        LoginResponseDto response = usuarioIService.login(dto);
        return ResponseEntity.ok(response);
    }

    // ── Recuperação de senha ──────────────────────────────────────────────────

    /**
     * POST /auth/recuperar-senha/solicitar
     *
     * Body: { "email": "usuario@email.com" }
     *
     * Gera um código de 6 dígitos, salva no banco e envia por e-mail.
     * Retorna 204 No Content (mesmo se o e-mail não existir, para não vazar dados).
     */
    @PostMapping(
            path = "/recuperar-senha/solicitar",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> solicitarCodigo(@RequestBody @Valid SolicitarCodigoRequestDto dto) {
        usuarioIService.solicitarCodigoRecuperacao(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /auth/recuperar-senha/validar
     *
     * Body: { "email": "usuario@email.com", "codigo": "123456" }
     *
     * Valida se o código está correto e dentro do prazo.
     * Retorna 204 No Content se válido, ou 422 com mensagem de erro se inválido/expirado.
     */
    @PostMapping(
            path = "/recuperar-senha/validar",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> validarCodigo(@RequestBody @Valid ValidarCodigoRequestDto dto) {
        usuarioIService.validarCodigo(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /auth/recuperar-senha/redefinir
     *
     * Body: { "email": "usuario@email.com", "codigo": "123456", "novaSenha": "nova123" }
     *
     * Valida o código e troca a senha. Limpa o token após o uso.
     * Retorna 204 No Content se bem-sucedido.
     */
    @PostMapping(
            path = "/recuperar-senha/redefinir",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequestDto dto) {
        usuarioIService.redefinirSenha(dto);
        return ResponseEntity.noContent().build();
    }
}