package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.infrastructure.security.JwtUtil;
import br.com.ifba.horizontemeu.usuario.dto.*;
import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // injetado do bean em SecurityConfig
    private final JwtUtil jwtUtil;

    // Chave da API do Brevo — configurada como variável de ambiente no Railway
    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        log.info("Buscando todos os usuários...");
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        log.info("Buscando usuário com id: {}", id);
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findByNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Garante que toda a operação seja executada como uma única transação.
    // Se qualquer etapa falhar, o banco de dados é revertido (rollback) automaticamente.
    @Transactional
    @Override
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            // Lança BusinessException — será capturada pelo ApiExceptionHandler
            throw new BusinessException("Dados do usuário não preenchidos.");
        }
        if (usuario.getId() != null) {
            // Impede salvar um usuário que já tem ID (já existe no banco)
            throw new BusinessException("Usuário já existente. Use o update.");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            // Garante que o e-mail seja único no sistema
            throw new BusinessException("E-mail já cadastrado: " + usuario.getEmail());
        }

        // Preenche a data de cadastro automaticamente
        usuario.setDataCadastro(LocalDate.now());

        // Perfil fixo como USUARIO — ninguém se auto-promove a ADMINISTRADOR via API
        // Se precisar criar um admin, isso é feito diretamente no banco ou por endpoint
        // separado protegido (não implementado aqui por ser específico de cada projeto)
        usuario.setPerfil(Perfil.USUARIO);

        // Criptografa a senha com BCrypt antes de salvar
        // BCrypt gera um salt aleatório internamente — senhas iguais geram hashes diferentes
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        log.info("Salvando novo usuário: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario update(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + id));

        // ALTERADO: antes lia "dto.getNome()" etc. — agora lê do objeto "usuario" (entidade) recebido
        existente.setNome(usuario.getNome());
        existente.setFotoPerfil(usuario.getFotoPerfil());
        existente.setBio(usuario.getBio());

        log.info("Atualizando usuário id: {}", id);
        return usuarioRepository.save(existente);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            // Lança BusinessException se o usuário não existir
            throw new BusinessException("Usuário não encontrado com id: " + id);
        }
        log.info("Removendo usuário id: {}", id);
        usuarioRepository.deleteById(id);
    }

    /**
     * Autentica o usuário e retorna o JWT.
     *
     * Fluxo:
     *   1. Busca o usuário pelo email
     *   2. Compara a senha enviada com o hash salvo (passwordEncoder.matches)
     *   3. Gera o token JWT com email e perfil
     *   4. Retorna o token + dados básicos do usuário
     *
     * Importante: a mensagem de erro é genérica ("Credenciais inválidas") mesmo quando
     * o email não existe. Isso evita que um atacante descubra quais emails estão cadastrados
     * (técnica chamada user enumeration).
     */
    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        // passwordEncoder.matches compara o texto puro com o hash BCrypt
        // Retorna false se a senha estiver errada
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas.");
        }

        // Gera o token JWT com email e perfil (ex: "USUARIO" ou "ADMINISTRADOR")
        String token = jwtUtil.gerarToken(
                usuario.getEmail(),
                usuario.getPerfil().name() // converte o Enum para String
        );

        log.info("Login realizado com sucesso para: {}", usuario.getEmail());

        // Retorna token + dados básicos — o front não precisa de outra chamada
        return new LoginResponseDto(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getFotoPerfil(),
                usuario.getBio()
        );
    }

    // ── Recuperação de senha (3 etapas) ──────────────────────────────────────

    /**
     * Etapa 1: gera um código numérico de 6 dígitos, salva com expiração de 1 hora
     * e envia por e-mail.
     *
     * Importante: mesmo que o e-mail não exista, retorna sem erro para não revelar
     * quais e-mails estão cadastrados (user enumeration prevention).
     */
    @Transactional
    @Override
    public void solicitarCodigoRecuperacao(SolicitarCodigoRequestDto dto) {
        Optional<Usuario> optional = usuarioRepository.findByEmail(dto.email());

        // Se o e-mail não existe, simplesmente ignoramos (sem erro ao front)
        if (optional.isEmpty()) {
            log.info("Solicitação de recuperação para e-mail não cadastrado: {}", dto.email());
            return;
        }

        Usuario usuario = optional.get();

        // Gera código de 6 dígitos (000000 a 999999)
        String codigo = String.format("%06d", new Random().nextInt(1_000_000));

        // Salva o código e define expiração em 1 hora a partir de agora
        usuario.setTokenResetSenha(codigo);
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        // Envia o código por e-mail via Brevo
        enviarEmailCodigo(usuario.getEmail(), usuario.getNome(), codigo);

        log.info("Código de recuperação enviado para: {}", usuario.getEmail());
    }

    /**
     * Etapa 2: valida se o código é correto e ainda não expirou.
     * Não altera a senha — apenas confirma que o código é válido.
     */
    @Override
    public void validarCodigo(ValidarCodigoRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BusinessException("Código inválido ou expirado."));

        validarTokenRecuperacao(usuario, dto.codigo());
    }

    /**
     * Etapa 3: valida o código mais uma vez e atualiza a senha.
     * Após trocar a senha, limpa o token para que não possa ser reutilizado.
     */
    @Transactional
    @Override
    public void redefinirSenha(RedefinirSenhaRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BusinessException("Código inválido ou expirado."));

        validarTokenRecuperacao(usuario, dto.codigo());

        // Troca a senha e limpa o token
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuario.setTokenResetSenha(null);
        usuario.setTokenExpiracao(null);
        usuarioRepository.save(usuario);

        log.info("Senha redefinida com sucesso para: {}", usuario.getEmail());
    }

    // ── Métodos auxiliares privados ───────────────────────────────────────────

    /**
     * Verifica se o código bate com o salvo e se ainda não expirou.
     * Lança BusinessException (HTTP 422) caso falhe.
     */
    private void validarTokenRecuperacao(Usuario usuario, String codigoInformado) {
        if (usuario.getTokenResetSenha() == null
                || usuario.getTokenExpiracao() == null
                || !usuario.getTokenResetSenha().equals(codigoInformado)
                || LocalDateTime.now().isAfter(usuario.getTokenExpiracao())) {
            throw new BusinessException("Código inválido ou expirado.");
        }
    }

    /**
     * Envia o e-mail com o código de recuperação usando a API HTTP do Brevo.
     * Não depende de porta SMTP — usa HTTPS, que sempre está liberado.
     * Funciona para qualquer destinatário sem precisar de domínio verificado.
     *
     */
    private void enviarEmailCodigo(String destinatario, String nome, String codigo) {
        try {
            // Monta o JSON do corpo da requisição no formato que o Brevo espera
            String corpo = String.format("""
                {
                    "sender": { "name": "Horizonte Meu", "email": "horizontemeu.adm@gmail.com" },
                    "to": [{ "email": "%s", "name": "%s" }],
                    "subject": "Horizonte Meu — Código de recuperação de senha",
                    "textContent": "Olá, %s!\\n\\nSeu código de verificação é: %s\\n\\nEste código é válido por 1 hora.\\n\\nEquipe Horizonte Meu"
                }
                """, destinatario, nome, nome, codigo);

            // Faz a requisição HTTP para a API do Brevo
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("E-mail enviado com sucesso via Brevo para: {}", destinatario);
            } else {
                log.error("Brevo retornou erro {}: {}", response.statusCode(), response.body());
            }

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail via Brevo para {}: {}", destinatario, e.getMessage());
            // Não propaga o erro para o front — o usuário não sabe se o e-mail foi enviado

        }
    }

    @Transactional
    @Override
    public void alterarSenha(Long id, AlterarSenhaRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + id));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
        log.info("Senha alterada para usuário id: {}", id);
    }
}