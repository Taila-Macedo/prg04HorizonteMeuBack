package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioIService {
    Page<Usuario> findAll(Pageable pageable);
    Optional<Usuario> findById(Long id);
    List<Usuario> findByNome(String nome);
    Usuario save(Usuario usuario);
    Usuario update(Long id, Usuario usuarioAtualizado);
    void delete(Long id);
}