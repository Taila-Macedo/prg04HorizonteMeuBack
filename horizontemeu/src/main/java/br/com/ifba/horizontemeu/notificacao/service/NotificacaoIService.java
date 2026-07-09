package br.com.ifba.horizontemeu.notificacao.service;

import br.com.ifba.horizontemeu.notificacao.entity.Notificacao;
import br.com.ifba.horizontemeu.notificacao.enums.TipoNotificacao;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;

import java.util.List;

public interface NotificacaoIService {

    List<Notificacao> findByUsuario(Long idUsuario);
    Notificacao criar(Usuario usuario, String mensagem, TipoNotificacao tipo);
    void marcarComoLida(Long id);
    void delete(Long id);
}
