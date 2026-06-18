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

                        // ── ROTAS PÚBLICAS (Ninguém precisa de Token aqui) ──
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // Fazer login
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll() // Criar conta (POST limpo em /usuarios)
                        .requestMatchers(HttpMethod.GET,  "/pontos-turisticos/findall").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/pontos-turisticos/findbyid/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/pontos-turisticos/findbynome").permitAll()

                        // Console do banco de dados de teste (Apenas para programar)
                        .requestMatchers("/h2-console/**").permitAll()

                        // ── ROTAS EXCLUSIVAS DO ADMINISTRADOR (Exige perfil ADMIN) ──
                        // Gerenciamento de pontos turísticos [cite: 14]
                        .requestMatchers(HttpMethod.POST,   "/pontos-turisticos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT,    "/pontos-turisticos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/pontos-turisticos/**").hasRole("ADMINISTRADOR")
                        // Aprovação de fotos da galeria [cite: 14, 105]
                        .requestMatchers("/fotos/aprovar/**").hasRole("ADMINISTRADOR")
                        // Excluir contas (DELETE limpo na rota /usuarios/{id})
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMINISTRADOR")

                        // ── QUALQUER OUTRA ROTA: Bloqueia tudo. É obrigatório estar logado ──
                        .anyRequest().authenticated()
                )

                // Ativa o nosso leitor de crachá (Filtro JWT) logo na entrada do sistema
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Permite o funcionamento do painel do banco de teste na tela
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }

    /**
     * Gerenciador de Autenticação do Spring Security.
     * Adicionado explicitamente para desativar a geração de senhas aleatórias no console.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * MOTOR DE CRIPTOGRAFIA: Serve para embaralhar as senhas dos usuários.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}