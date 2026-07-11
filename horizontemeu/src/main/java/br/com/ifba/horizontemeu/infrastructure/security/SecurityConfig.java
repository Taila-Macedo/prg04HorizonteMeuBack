package br.com.ifba.horizontemeu.infrastructure.security;

import br.com.ifba.horizontemeu.infrastructure.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilita o CORS usando o CorsConfig (CorsConfigurationSource) registrado no contexto
                .cors(withDefaults())

                // Desativa CSRF — usamos JWT, não cookies
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless — sem sessão HTTP
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ── ROTAS PÚBLICAS ────────────────────────────────────────────────
                        // Preflight OPTIONS — nunca bloquear
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-senha/solicitar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-senha/validar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-senha/redefinir").permitAll()

                        // Galeria pública
                        .requestMatchers(HttpMethod.GET, "/fotos/ponto/**").permitAll()
                        // Comentários de um ponto — público
                        .requestMatchers(HttpMethod.GET, "/comentarios/ponto/**").permitAll()
                        // Roteiro público por ID (RN16)
                        .requestMatchers(HttpMethod.GET, "/roteiros/{id}").permitAll()
                        // Qualquer leitura de pontos turísticos é pública
                        .requestMatchers(HttpMethod.GET, "/pontos/**").permitAll()

                        // ── ROTAS EXCLUSIVAS DO ADMINISTRADOR ────────────────────────────
                        .requestMatchers(HttpMethod.POST,   "/pontos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT,    "/pontos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/pontos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/fotos/aprovar/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/fotos/aprovacao").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/denuncias").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/denuncias/{id}").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/denuncias/status/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/denuncias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/denuncias/**").hasRole("ADMINISTRADOR")

                        // ── QUALQUER OUTRA ROTA: exige autenticação ───────────────────────
                        .anyRequest().authenticated()
                )

                // Sem isso, o Spring Security usa o handler padrão (Http403ForbiddenEntryPoint),
                // que devolve 403 tanto pra "sem token/token expirado" quanto pra "sem permissão".
                // Isso confunde o frontend, que não consegue diferenciar "faça login de novo"
                // de "você não tem permissão pra isso". Agora:
                //   401 → não autenticado (sem token, token expirado ou inválido)
                //   403 → autenticado, mas sem o perfil/role necessário
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED,
                                        "Sessão expirada ou inválida. Faça login novamente."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                escreverErro(response, HttpStatus.FORBIDDEN,
                                        "Você não tem permissão para acessar este recurso."))
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Escreve o corpo de erro no mesmo formato usado pelo ApiExceptionHandler (ErrorResponse),
    // pra manter a resposta consistente em toda a API.
    private void escreverErro(HttpServletResponse response, HttpStatus status, String mensagem) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse erro = new ErrorResponse(status.value(), mensagem, null, null);
        new ObjectMapper().writeValue(response.getWriter(), erro);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}