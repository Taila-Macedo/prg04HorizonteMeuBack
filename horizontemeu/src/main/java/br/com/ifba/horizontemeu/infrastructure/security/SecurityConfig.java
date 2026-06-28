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

                        // ── QUALQUER OUTRA ROTA: exige autenticação ───────────────────────
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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