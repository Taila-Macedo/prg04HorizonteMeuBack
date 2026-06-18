package br.com.ifba.horizontemeu.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * Utilitátio central para operações com JWT
 *
 * Responsabilidades:
 *   -Gerar tokens
 *   -Extrair informações (email, perfil) de um token recebido
 *   -Validar se um token é genuíno e não está expirado
 *
 * POR QUE USAMOS HMAC-SHA256 NO PROJETO:
 *   1. Arquitetura Stateless: Permite que o Spring Boot e o React conversem via tokens, sem salvar sessão no servidor.
 *   2. Segurança e Integridade: Funciona como um "lacre digital". Se o usuário alterar o token no navegador (ex: tentar virar ADMIN), a assinatura quebra no Back-end e o acesso é negado na hora.
 *
 */
@Component
public class JwtUtil {

    // Chave criptográfica derivada da string no application.properties
    private final SecretKey secretKey;

    // Tempo de vida do token em milissegundos (vem do application.properties)
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Keys.hmacShaKeyFor converte a string em uma SecretKey segura para HMAC-SHA
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Cria um novo token para o usuário após ele colocar a senha correta.
     *
     * Estrutura do payload gerado:
     *   { "sub": "usuario@email.com", "perfil": "USUARIO", "iat": ..., "exp": ... }
     */
    public String gerarToken(String email, String perfil) {
        return Jwts.builder()
                .subject(email)                                          // quem é o dono do token
                .claim("perfil", perfil)                           // Permissão extra: Ele é USUARIO comum ou ADMINISTRADOR?
                .issuedAt(new Date())                                    // momento de criação
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // quando expira
                .signWith(secretKey)                                     // assina com HMAC-SHA256
                .compact();                                              // Finalização: Junta tudo e transforma em uma única linha de texto codificado
    }

    /**
     * Extrai o email do token(o e-mail do usuário).
     */
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    /**
     * Extrai o perfil do token.
     */
    public String extrairPerfil(String token) {
        return extrairClaims(token).get("perfil", String.class);
    }

    /**
     * Verifica se o token é válido: assinatura correta E não expirado.
     * Retorna false para qualquer exceção (token adulterado, expirado, malformado).
     */
    public boolean isTokenValido(String token) {
        try {
            extrairClaims(token); // se lançar qualquer exceção, o token é inválido
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Método auxiliar privado: faz o parse completo do token e retorna os Claims.
     * Qualquer problema (assinatura errada, token expirado) lança exceção aqui.
     */
    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)   // usa a mesma chave que usou para assinar
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
