package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.infrastructure.security.JwtUtil;
import br.com.ifba.horizontemeu.usuario.dto.LoginRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.LoginResponseDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPutRequestDto;
import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // injetado do bean em SecurityConfig
    private final JwtUtil jwtUtil;
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
    public Usuario update(Long id, UsuarioPutRequestDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + id));

        // Atualiza APENAS nome e foto — email e senha têm fluxos próprios
        existente.setNome(dto.getNome());
        existente.setFotoPerfil(dto.getFotoPerfil());

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
                usuario.getPerfil().name()
        );
    }
}