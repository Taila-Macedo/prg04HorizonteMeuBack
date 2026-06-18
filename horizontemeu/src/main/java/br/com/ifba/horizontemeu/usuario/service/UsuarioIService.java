package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.usuario.dto.LoginRequestDto;
import br.com.ifba.horizontemeu.usuario.dto.LoginResponseDto;
import br.com.ifba.horizontemeu.usuario.dto.UsuarioPutRequestDto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioIService {

    Page<Usuario> findAll(Pageable pageable);

    Optional<Usuario> findById(Long id);

    List<Usuario> findByNome(String nome);

    // Recebe a entidade montada pelo controller (sem senha criptografada ainda)
    Usuario save(Usuario usuario);

    // Recebe o DTO de update diretamente — não usa a entidade crua
    // para evitar que campos sensíveis (email, senha) sejam sobrescritos
    Usuario update(Long id, UsuarioPutRequestDto dto);

    void delete(Long id);

    // Novo: autentica o usuário e retorna o JWT
    LoginResponseDto login(LoginRequestDto dto);
}