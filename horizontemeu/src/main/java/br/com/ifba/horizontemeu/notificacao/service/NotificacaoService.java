package br.com.ifba.horizontemeu.notificacao.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.notificacao.entity.Notificacao;
import br.com.ifba.horizontemeu.notificacao.enums.TipoNotificacao;
import br.com.ifba.horizontemeu.notificacao.repository.NotificacaoRepository;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService implements NotificacaoIService{

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    @Override
    public List<Notificacao> findByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o id: " + idUsuario));

        log.info("Buscando notificações do usuário id: {}", idUsuario);
        return notificacaoRepository.findByUsuarioOrderByDataDesc(usuario);
    }

    @Transactional
    @Override
    public Notificacao criar(Usuario usuario, String mensagem, TipoNotificacao tipo) {
        if (usuario == null) {
            throw new BusinessException("Usuário da notificação não pode ser nulo.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new BusinessException("A mensagem da notificação não pode ser vazia.");
        }
        if (tipo == null) {
            throw new BusinessException("O tipo da notificação não pode ser nulo.");
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setLida(false);
        notificacao.setData(LocalDateTime.now());

        log.info("Criando notificação tipo {} para o usuário {}", tipo, usuario.getEmail());
        return notificacaoRepository.save(notificacao);
    }


    @Transactional
    @Override
    public void marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Notificação não encontrada com id: " + id));

        notificacao.setLida(true);

        log.info("Marcando notificação id: {} como lida", id);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!notificacaoRepository.existsById(id)) {
            throw new BusinessException("Notificação não encontrada com id: " + id);
        }

        log.info("Removendo notificação id: {}", id);
        notificacaoRepository.deleteById(id);
    }
}
