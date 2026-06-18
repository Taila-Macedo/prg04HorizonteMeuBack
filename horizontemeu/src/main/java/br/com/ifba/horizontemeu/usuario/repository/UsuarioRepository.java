package br.com.ifba.horizontemeu.usuario.repository;

import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Usado no login e na validação de e-mail único (RN01)
    Optional<Usuario> findByEmail(String email);

    // Verifica se e-mail já existe sem trazer o objeto inteiro — mais eficiente (RN01)
    boolean existsByEmail(String email);

    // Busca pelo nome ignorando maiúsculas/minúsculas
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Busca por perfil
    List<Usuario> findByPerfil(Perfil perfil);

    // Busca pelo token de reset APENAS se ainda não expirou (RN20)
    // Só vai ser usado quando implementar o fluxo de redefinição de senha
    @Query("SELECT u FROM Usuario u WHERE u.tokenResetSenha = :token AND u.tokenExpiracao > CURRENT_TIMESTAMP")
    Optional<Usuario> findByTokenResetSenhaValido(@Param("token") String token);
}