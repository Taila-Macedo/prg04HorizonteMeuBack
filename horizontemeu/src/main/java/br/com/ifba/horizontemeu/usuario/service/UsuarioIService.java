package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.usuario.dto.*;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioIService {

    Page<Usuario> findAll(Pageable pageable);
    Optional<Usuario> findById(Long id);
    List<Usuario> findByNome(String nome);
    Usuario save(Usuario usuario);
    Usuario update(Long id, UsuarioPutRequestDto dto);
    void delete(Long id);
    LoginResponseDto login(LoginRequestDto dto);
    void alterarSenha(Long id, AlterarSenhaRequestDto dto);

    // ── Recuperação de senha (3 etapas) ─────────────────────────────────────

    /** Etapa 1: gera um código de 6 dígitos e envia por e-mail. */
    void solicitarCodigoRecuperacao(SolicitarCodigoRequestDto dto);

    /** Etapa 2: valida o código sem redefinir ainda a senha. */
    void validarCodigo(ValidarCodigoRequestDto dto);

    /** Etapa 3: valida o código novamente e atualiza a senha. */
    void redefinirSenha(RedefinirSenhaRequestDto dto);
}