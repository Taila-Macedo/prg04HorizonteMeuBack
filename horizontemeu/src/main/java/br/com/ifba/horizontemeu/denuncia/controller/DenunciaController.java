package br.com.ifba.horizontemeu.denuncia.controller;

import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.denuncia.dto.DenunciaPostRequestDto;
import br.com.ifba.horizontemeu.denuncia.entity.Denuncia;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import br.com.ifba.horizontemeu.denuncia.mapper.DenunciaMapper;
import br.com.ifba.horizontemeu.denuncia.service.DenunciaIService;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/denuncias")
@RequiredArgsConstructor
public class DenunciaController implements DenunciaIController {

    private final DenunciaIService denunciaIService;
    private final DenunciaMapper denunciaMapper;

    /**
     * Lista todas as denúncias paginadas.
     * GET /denuncias?page=0&size=10
     * Requer: ADMINISTRADOR
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<?>> findAll(Pageable pageable) {
        return ResponseEntity.ok(
                denunciaIService.findAll(pageable).map(denunciaMapper::toDto));
    }

    /**
     * Busca denúncia por ID.
     * GET /denuncias/{id}
     * Requer: ADMINISTRADOR
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return denunciaIService.findById(id)
                .<ResponseEntity<?>>map(d -> ResponseEntity.ok(denunciaMapper.toDto(d)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Denúncia não encontrada com id: " + id));
    }

    /**
     * Lista as denúncias feitas por um usuário.
     * GET /denuncias/usuario/{idUsuario}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                denunciaMapper.toDtoList(denunciaIService.findByUsuario(idUsuario)));
    }

    /**
     * Lista denúncias filtradas por status — usado na fila de moderação do admin.
     * GET /denuncias/status/{status}?page=0&size=10  (status = PENDENTE | RESOLVIDA | REJEITADA)
     * Requer: ADMINISTRADOR
     */
    @Override
    @GetMapping(path = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByStatus(@PathVariable StatusDenuncia status, Pageable pageable) {
        return ResponseEntity.ok(
                denunciaIService.findByStatus(status, pageable).map(denunciaMapper::toDto));
    }

    /**
     * Registra uma nova denúncia (enviar()) — status inicial sempre PENDENTE.
     * POST /denuncias
     * Requer: autenticação
     * RN05: apenas um dos três alvos deve vir preenchido no corpo.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> enviar(@RequestBody @Valid DenunciaPostRequestDto dto) {
        Denuncia denuncia = new Denuncia();
        denuncia.setMotivo(dto.getMotivo());

        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        denuncia.setUsuario(usuario);

        if (dto.getIdFoto() != null) {
            Foto foto = new Foto();
            foto.setId(dto.getIdFoto());
            denuncia.setFoto(foto);
        }

        if (dto.getIdComentario() != null) {
            Comentario comentario = new Comentario();
            comentario.setId(dto.getIdComentario());
            denuncia.setComentario(comentario);
        }

        if (dto.getIdUsuarioDenunciado() != null) {
            Usuario usuarioDenunciado = new Usuario();
            usuarioDenunciado.setId(dto.getIdUsuarioDenunciado());
            denuncia.setUsuarioDenunciado(usuarioDenunciado);
        }

        Denuncia salva = denunciaIService.enviar(denuncia);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(denunciaMapper.toDto(salva));
    }

    /**
     * Admin marca a denúncia como resolvida (após tomar a ação necessária).
     * PATCH /denuncias/{id}/resolver
     * Requer: ADMINISTRADOR
     */
    @Override
    @PatchMapping(path = "/{id}/resolver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaMapper.toDto(denunciaIService.resolver(id)));
    }

    /**
     * Admin descarta a denúncia como inválida.
     * PATCH /denuncias/{id}/rejeitar
     * Requer: ADMINISTRADOR
     */
    @Override
    @PatchMapping(path = "/{id}/rejeitar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaMapper.toDto(denunciaIService.rejeitar(id)));
    }

    /**
     * Admin resolve a denúncia E exclui o conteúdo denunciado (foto ou comentário), atomicamente.
     * PATCH /denuncias/{id}/resolver-excluindo
     * Requer: ADMINISTRADOR
     */
    @Override
    @PatchMapping(path = "/{id}/resolver-excluindo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolverExcluindoConteudo(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaMapper.toDto(denunciaIService.resolverExcluindoConteudo(id)));
    }

    /**
     * Remove uma denúncia pelo ID.
     * DELETE /denuncias/{id}
     * Requer: ADMINISTRADOR
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        denunciaIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}