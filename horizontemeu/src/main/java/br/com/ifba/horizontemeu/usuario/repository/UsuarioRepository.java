package br.com.ifba.horizontemeu.usuario.repository;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca pelo e-mail
    Optional<Usuario> findByEmail(String email);

    // Busca pelo nome, ignorando maiúsculas/minúsculas
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Busca por perfil
    List<Usuario> findByPerfil(String perfil);
}