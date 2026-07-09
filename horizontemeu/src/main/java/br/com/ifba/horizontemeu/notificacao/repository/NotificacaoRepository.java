package br.com.ifba.horizontemeu.notificacao.repository;

import br.com.ifba.horizontemeu.notificacao.entity.Notificacao;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {


    //Lista as notificações de um usuário, mais recente primeiro
    List<Notificacao> findByUsuarioOrderByDataDesc(Usuario usuario);
}
