package br.com.ifba.horizontemeu.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

/**
 * Configuração central do Spring Security.
 *
 * @EnableWebSecurity — ativa o módulo de segurança web do Spring
 * @EnableMethodSecurity — habilita @PreAuthorize nos métodos de service/controller
 *
 * Política geral: STATELESS (sem sessão HTTP — cada request precisa do token JWT)
 *
 * Rotas públicas (sem token):
 *   POST /auth/login       — fazer login
 *   POST /usuarios         — criar conta
 *   GET  /pontos/**        — qualquer leitura de pontos turísticos
 *
 * Rotas exclusivas de ADMINISTRADOR:
 *   POST/PUT/DELETE /pontos/**     — cadastrar, editar e remover pontos
 *   /fotos/aprovar/**              — aprovar fotos enviadas por usuários
 *   DELETE /usuarios/**            — remover usuários
 *
 * Qualquer outra rota: exige token válido (qualquer perfil)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desativa a proteção contra ataques baseados em Cookies, já que usamos Tokens JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Configura a API para ser Stateless: o servidor não guarda sessões na memória
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ── ROTAS PÚBLICAS (sem token) ────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Galeria pública — qualquer um pode ver fotos aprovadas de um ponto
                        .requestMatchers(HttpMethod.GET, "/fotos/ponto/**").permitAll()
                        // Comentários de um ponto — público, qualquer um pode ler
                        .requestMatchers(HttpMethod.GET, "/comentarios/ponto/**").permitAll()
                        // Roteiro público — acessível por link sem autenticação (RN16)
                        // Só o GET por ID é público — listagem e edição exigem autenticação
                        .requestMatchers(HttpMethod.GET, "/roteiros/{id}").permitAll()

                        // Qualquer leitura de pontos turísticos é pública
                        // visitantes podem explorar o mapa sem estar logados
                        .requestMatchers(HttpMethod.GET, "/pontos/**").permitAll()

                        // Console H2 — apenas desenvolvimento, REMOVER em produção
                        .requestMatchers("/h2-console/**").permitAll()

                        // ── ROTAS EXCLUSIVAS DO ADMINISTRADOR ────────────────────────────
                        // Só admin cadastra, edita e remove pontos turísticos (RN07)
                        .requestMatchers(HttpMethod.POST,   "/pontos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT,    "/pontos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/pontos/**").hasRole("ADMINISTRADOR")

                        // Só admin aprova fotos enviadas pelos usuários (RN08)
                        .requestMatchers("/fotos/aprovar/**").hasRole("ADMINISTRADOR")

                        // Lista de fotos pendentes — só admin vê (RN08)
                        .requestMatchers(HttpMethod.GET, "/fotos/aprovacao").hasRole("ADMINISTRADOR")

                        // Só admin pode deletar contas de usuários (RN13)
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMINISTRADOR")

                        // ── QUALQUER OUTRA ROTA: exige autenticação ───────────────────────
                        .anyRequest().authenticated()
                )

                // Registra o filtro JWT antes do filtro padrão do Spring Security
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Permite iframe do H2 console — REMOVER em produção
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }

    /**
     * Gerenciador de Autenticação do Spring Security.
     * Declarado explicitamente para desativar a geração de senhas aleatórias no console.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Bean de BCrypt — motor de criptografia das senhas.
     * Declarado aqui para ficar centralizado na configuração de segurança.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}