package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // Define perfil padrão caso não seja informado
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(Perfil.USUARIO);
        }

        log.info("Salvando novo usuário: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario update(Long id, Usuario usuarioAtualizado) {
        // Lança BusinessException se o usuário não for encontrado
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + id));

        existente.setNome(usuarioAtualizado.getNome());
        existente.setFotoPerfil(usuarioAtualizado.getFotoPerfil());

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
}