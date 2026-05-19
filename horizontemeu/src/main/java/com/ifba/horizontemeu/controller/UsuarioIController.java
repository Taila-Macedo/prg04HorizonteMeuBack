package com.ifba.horizontemeu.controller;

import com.ifba.horizontemeu.entity.Usuario;
import java.util.List;

public interface UsuarioIController {
    public List<Usuario> findAll();
    public Usuario findById(Long id);
    public List<Usuario> findByNome(String nome);
    public Usuario save(Usuario usuario);
    public Usuario update(Long id, Usuario usuario);
    public void delete(Long id);
}