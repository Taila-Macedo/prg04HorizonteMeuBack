package br.com.ifba.horizontemeu.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT que intercepta todas as requisições HTTP.
 *
 * OncePerRequestFilter garante que este filtro roda exatamente uma vez
 * por requisição, mesmo em chains de filtros complexos.
 *
 * Fluxo:
 *   1. Lê o cabeçalho "Authorization: Bearer <token>"
 *   2. Se não houver token ou ele for inválido → deixa passar sem autenticar
 *      (o Spring Security vai bloquear se a rota for protegida)
 *   3. Se o token for válido → extrai email e perfil, monta a autenticação
 *      e registra no SecurityContextHolder para que o resto da aplicação veja
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Sem cabeçalho Authorization ou sem o prefixo "Bearer " → segue sem autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Remove o prefixo "Bearer" para pegar só o token
        String token = authHeader.substring(7);

        //Token inválido -> segue sem autenticar
        if(!jwtUtil.isTokenValido(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        //Token válido: extrai as informações do usuário
        String email = jwtUtil.extrairEmail(token);
        String perfil = jwtUtil.extrairPerfil(token);

        // Cria a authority no formato que o Spring Security espera: "ROLE_USUARIO" ou "ROLE_ADMINISTRADOR"
        // Isso é o que permite usar hasRole("ADMINISTRADOR") nas rotas e @PreAuthorize
        var authority = new SimpleGrantedAuthority("ROLE_" + perfil);

        // UsernamePasswordAuthenticationToken com 3 argumentos = já autenticado
        // (com 2 argumentos seria apenas credenciais ainda não verificadas)
        var authentication = new UsernamePasswordAuthenticationToken(
                email,   //Quem está autenticado
                null,   // não precisamos guardar a senha aqui
                List.of(authority)
        );

        //Associa detalhes da requisição à autenticação
        authentication .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //Registra a autenticação no contexto da thread atual
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Continua a chain de filtros normalmente
        filterChain.doFilter(request, response);

    }
}
