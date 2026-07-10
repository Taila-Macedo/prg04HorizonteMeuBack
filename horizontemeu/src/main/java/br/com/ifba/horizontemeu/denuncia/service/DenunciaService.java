package br.com.ifba.horizontemeu.denuncia.service;

import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.comentario.repository.ComentarioRepository;
import br.com.ifba.horizontemeu.comentario.service.ComentarioIService;
import br.com.ifba.horizontemeu.denuncia.entity.Denuncia;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import br.com.ifba.horizontemeu.denuncia.repository.DenunciaRepository;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.foto.repository.FotoRepository;
import br.com.ifba.horizontemeu.foto.service.FotoIService;
import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.notificacao.enums.TipoNotificacao;
import br.com.ifba.horizontemeu.notificacao.service.NotificacaoIService;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DenunciaService implements DenunciaIService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FotoRepository fotoRepository;
    private final ComentarioRepository comentarioRepository;
    private final FotoIService fotoIService;
    private final ComentarioIService comentarioIService;
    // NOVO — usado para avisar o autor do conteúdo quando ele é removido por denúncia
    private final NotificacaoIService notificacaoIService;

    private static final Logger log = LoggerFactory.getLogger(DenunciaService.class);

    @Override
    public Page<Denuncia> findAll(Pageable pageable) {
        log.info("Buscando todas as denúncias...");
        return denunciaRepository.findAll(pageable);
    }

    @Override
    public Optional<Denuncia> findById(Long id) {
        log.info("Buscando denúncia com id: {}", id);
        return denunciaRepository.findById(id);
    }

    @Override
    public List<Denuncia> findByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + idUsuario));

        log.info("Buscando denúncias feitas pelo usuário id: {}", idUsuario);
        return denunciaRepository.findByUsuario(usuario);
    }

    @Override
    public Page<Denuncia> findByStatus(StatusDenuncia status, Pageable pageable) {
        log.info("Buscando denúncias com status: {}", status);
        return denunciaRepository.findByStatus(status, pageable);
    }

    @Transactional
    @Override
    public Denuncia enviar(Denuncia denuncia) {
        if (denuncia == null) {
            throw new BusinessException("Dados da denúncia não preenchidos.");
        }
        if (denuncia.getMotivo() == null || denuncia.getMotivo().isBlank()) {
            throw new BusinessException("O motivo da denúncia não pode ser vazio.");
        }
        if (denuncia.getUsuario() == null || denuncia.getUsuario().getId() == null) {
            throw new BusinessException("O usuário que está denunciando é obrigatório.");
        }

        // RN05 — apenas uma das três FKs pode estar preenchida
        boolean temFoto = denuncia.getFoto() != null && denuncia.getFoto().getId() != null;
        boolean temComentario = denuncia.getComentario() != null && denuncia.getComentario().getId() != null;
        boolean temUsuarioDenunciado = denuncia.getUsuarioDenunciado() != null
                && denuncia.getUsuarioDenunciado().getId() != null;

        long preenchidos = (temFoto ? 1 : 0) + (temComentario ? 1 : 0) + (temUsuarioDenunciado ? 1 : 0);

        if (preenchidos == 0) {
            throw new BusinessException(
                    "A denúncia precisa indicar um alvo: foto, comentário ou usuário denunciado.");
        }
        if (preenchidos > 1) {
            throw new BusinessException(
                    "A denúncia só pode ter um alvo por vez — foto, comentário ou usuário, não mais de um.");
        }

        Usuario usuario = usuarioRepository.findById(denuncia.getUsuario().getId())
                .orElseThrow(() -> new BusinessException(
                        "Usuário não encontrado com id: " + denuncia.getUsuario().getId()));
        denuncia.setUsuario(usuario);

        if (temFoto) {
            Foto foto = fotoRepository.findById(denuncia.getFoto().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Foto não encontrada com id: " + denuncia.getFoto().getId()));
            denuncia.setFoto(foto);
            denuncia.setComentario(null);
            denuncia.setUsuarioDenunciado(null);

        } else if (temComentario) {
            Comentario comentario = comentarioRepository.findById(denuncia.getComentario().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Comentário não encontrado com id: " + denuncia.getComentario().getId()));
            denuncia.setComentario(comentario);
            denuncia.setFoto(null);
            denuncia.setUsuarioDenunciado(null);

        } else {
            Usuario usuarioDenunciado = usuarioRepository.findById(denuncia.getUsuarioDenunciado().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Usuário denunciado não encontrado com id: " + denuncia.getUsuarioDenunciado().getId()));

            if (usuarioDenunciado.getId().equals(usuario.getId())) {
                throw new BusinessException("Você não pode denunciar o próprio perfil.");
            }

            denuncia.setUsuarioDenunciado(usuarioDenunciado);
            denuncia.setFoto(null);
            denuncia.setComentario(null);
        }

        // enviar() — RN: toda denúncia nasce com status PENDENTE
        denuncia.setStatus(StatusDenuncia.PENDENTE);
        denuncia.setData(LocalDateTime.now());

        log.info("Registrando denúncia do usuário {} — motivo: {}", usuario.getEmail(), denuncia.getMotivo());
        return denunciaRepository.save(denuncia);

        // Nota (RN15): nenhuma Notificacao é disparada aqui —
        // o sistema não avisa o denunciante sobre o resultado da própria denúncia.
    }

    @Transactional
    @Override
    public Denuncia resolver(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Denúncia não encontrada com id: " + id));

        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Só é possível resolver denúncias pendentes.");
        }

        denuncia.setStatus(StatusDenuncia.RESOLVIDA);

        log.info("Denúncia id: {} marcada como RESOLVIDA (sem exclusão de conteúdo)", id);
        return denunciaRepository.save(denuncia);
    }

    /**
     * Resolve a denúncia E exclui o conteúdo denunciado (foto ou comentário),
     * notificando o autor do conteúdo removido. Tudo em uma única transação.
     */
    @Transactional
    @Override
    public Denuncia resolverExcluindoConteudo(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Denúncia não encontrada com id: " + id));

        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Só é possível resolver denúncias pendentes.");
        }

        if (denuncia.getFoto() != null) {
            Foto foto = denuncia.getFoto();
            Usuario autor = foto.getUsuario();

            desvincularDenunciasDaFoto(foto);
            fotoIService.delete(foto.getId());

            notificacaoIService.criar(
                    autor,
                    "Sua foto foi removida por violar as diretrizes da plataforma.",
                    TipoNotificacao.CONTEUDO_REMOVIDO
            );

        } else if (denuncia.getComentario() != null) {
            Comentario comentario = denuncia.getComentario();
            Usuario autor = comentario.getUsuario();

            desvincularDenunciasDoComentario(comentario);
            comentarioIService.delete(comentario.getId()); // já recalcula notaMedia do ponto (RN04)

            notificacaoIService.criar(
                    autor,
                    "Seu comentário foi removido por violar as diretrizes da plataforma.",
                    TipoNotificacao.CONTEUDO_REMOVIDO
            );

        } else {
            throw new BusinessException(
                    "Denúncias de perfil de usuário não excluem conteúdo automaticamente — "
                            + "use a gestão de usuários para essa ação.");
        }

        denuncia.setStatus(StatusDenuncia.RESOLVIDA);

        log.info("Denúncia id: {} resolvida com exclusão do conteúdo denunciado", id);
        return denunciaRepository.save(denuncia);
    }

    @Transactional
    @Override
    public Denuncia rejeitar(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Denúncia não encontrada com id: " + id));

        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Só é possível rejeitar denúncias pendentes.");
        }

        denuncia.setStatus(StatusDenuncia.REJEITADA);

        log.info("Denúncia id: {} marcada como REJEITADA", id);
        return denunciaRepository.save(denuncia);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!denunciaRepository.existsById(id)) {
            throw new BusinessException("Denúncia não encontrada com id: " + id);
        }

        log.info("Removendo denúncia id: {}", id);
        denunciaRepository.deleteById(id);
    }

    // Remove a referência da foto em TODAS as denúncias que apontam pra ela —
    // senão a exclusão da foto quebra a FK das outras denúncias existentes.
    private void desvincularDenunciasDaFoto(Foto foto) {
        List<Denuncia> denuncias = denunciaRepository.findByFoto(foto);
        for (Denuncia d : denuncias) {
            d.setFoto(null);
            denunciaRepository.save(d);
        }
    }

    private void desvincularDenunciasDoComentario(Comentario comentario) {
        List<Denuncia> denuncias = denunciaRepository.findByComentario(comentario);
        for (Denuncia d : denuncias) {
            d.setComentario(null);
            denunciaRepository.save(d);
        }
    }
}