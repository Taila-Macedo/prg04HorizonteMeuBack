package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    /** Busca todos os usuários cadastrados */
    @Override
    public List<Usuario> findAll() {
        log.info("Buscando todos os usuários...");
        return usuarioRepository.findAll();
    }

    /** Busca o usuário por id */
    @Override
    public Optional<Usuario> findById(Long id) {
        log.info("Buscando usuário com id: {}", id);
        return usuarioRepository.findById(id);
    }

    /** Busca usuários pelo nome */
    @Override
    public List<Usuario> findByNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Cadastra um novo usuário.
     * Regras:
     *  - Dados não podem ser nulos
     *  - E-mail deve ser único no sistema
     *  - Perfil padrão é "usuario"
     *  - Data de cadastro é preenchida automaticamente
     */
    @Override
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new RuntimeException("Dados do usuário não preenchidos.");
        }
        if (usuario.getId() != null) {
            throw new RuntimeException("Usuário já existente. Use o update.");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado: " + usuario.getEmail());
        }

        usuario.setDataCadastro(LocalDate.now());
        if (usuario.getPerfil() == null || usuario.getPerfil().isBlank()) {
            usuario.setPerfil("usuario");
        }

        log.info("Salvando novo usuário: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * Atualiza apenas nome e foto de perfil.
     */
    @Override
    public Usuario update(Long id, Usuario usuarioAtualizado) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));

        existente.setNome(usuarioAtualizado.getNome());
        existente.setFotoPerfil(usuarioAtualizado.getFotoPerfil());

        log.info("Atualizando usuário id: {}", id);
        return usuarioRepository.save(existente);
    }

    /** Remove um usuário pelo ID */
    @Override
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com id: " + id);
        }
        log.info("Removendo usuário id: {}", id);
        usuarioRepository.deleteById(id);
    }
}