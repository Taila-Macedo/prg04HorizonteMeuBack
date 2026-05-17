package com.ifba.horizontemeu.repository;

import com.ifba.horizontemeu.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //buscar pelo e-mail
    Optional<Usuario> findByEmail(String email);

    // Busca pelo nome, ignorando maiúsculas/minúsculas
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    //busca por perfil
    List<Usuario> findByPerfil(String nome);
}
