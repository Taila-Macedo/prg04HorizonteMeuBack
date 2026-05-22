package br.com.ifba.horizontemeu.usuario.service;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioIService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    List<Usuario> findByNome(String nome);
    Usuario save(Usuario usuario);
    Usuario update(Long id, Usuario usuarioAtualizado);
    void delete(Long id);
}