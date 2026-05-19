package com.ifba.horizontemeu.service;

import com.ifba.horizontemeu.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioIService {

    public List<Usuario> findAll();
    public Optional<Usuario> findById(Long id);
    public List<Usuario> findByNome(String nome);
    public Usuario save(Usuario usuario);
    public Usuario update(Long id, Usuario usuarioAtualizado);
    public void delete(Long id);
}
