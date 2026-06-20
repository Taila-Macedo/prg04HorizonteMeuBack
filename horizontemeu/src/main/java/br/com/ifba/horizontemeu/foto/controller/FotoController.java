package br.com.ifba.horizontemeu.foto.controller;

import br.com.ifba.horizontemeu.foto.dto.FotoGetResponseDto;
import br.com.ifba.horizontemeu.foto.dto.FotoPostRequestDto;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.foto.mapper.FotoMapper;
import br.com.ifba.horizontemeu.foto.service.FotoIService;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
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
@RequestMapping("/fotos")
@RequiredArgsConstructor
public class FotoController implements FotoIController {

    private final FotoIService fotoIService;

    // FotoMapper injetado no lugar do ObjectMapperUtil para conversões de Foto
    // Os outros módulos continuam usando ObjectMapperUtil normalmente
    private final FotoMapper fotoMapper;

    /**
     * Lista todas as fotos paginadas.
     * GET /fotos?page=0&size=10
     * Requer: autenticação
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<FotoGetResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(
                fotoIService.findAll(pageable)
                        // fotoMapper.toDto() resolve os campos aninhados corretamente
                        .map(fotoMapper::toDto));
    }

    /**
     * Busca foto por ID.
     * GET /fotos/{id}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return fotoIService.findById(id)
                .<ResponseEntity<?>>map(f -> ResponseEntity.ok(fotoMapper.toDto(f)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Foto não encontrada com id: " + id));
    }

    /**
     * Busca todas as fotos de um ponto turístico.
     * GET /fotos/ponto/{idPonto}
     * Requer: público — galeria do ponto é visível para todos
     */
    @Override
    @GetMapping(path = "/ponto/{idPonto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPontoTuristico(@PathVariable Long idPonto) {
        return ResponseEntity.ok(
                fotoMapper.toDtoList(fotoIService.findByPontoTuristico(idPonto)));
    }

    /**
     * Busca fotos aprovadas ou pendentes.
     * GET /fotos/aprovacao?aprovado=false
     * Requer: ADMINISTRADOR — só admin vê fotos pendentes (RN08)
     */
    @Override
    @GetMapping(path = "/aprovacao", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByAprovado(@RequestParam Boolean aprovado) {
        return ResponseEntity.ok(
                fotoMapper.toDtoList(fotoIService.findByAprovado(aprovado)));
    }

    /**
     * Envia uma nova foto para a galeria de um ponto.
     * POST /fotos
     * Requer: autenticação (qualquer usuário logado pode enviar foto)
     * aprovado começa como false — aguarda aprovação do admin (RN08)
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid FotoPostRequestDto dto) {
        Foto foto = new Foto();
        foto.setUrl(dto.getUrl());
        foto.setLegenda(dto.getLegenda());

        // Cria objetos com só o ID para o service buscar e validar no banco
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        foto.setUsuario(usuario);

        PontoTuristico ponto = new PontoTuristico();
        ponto.setId(dto.getIdPontoTuristico());
        foto.setPontoTuristico(ponto);

        Foto salva = fotoIService.save(foto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fotoMapper.toDto(salva));
    }

    /**
     * Admin aprova a foto para aparecer na galeria.
     * PATCH /fotos/aprovar/{id}
     * Requer: ADMINISTRADOR — protegido no SecurityConfig (RN08)
     */
    @Override
    @PatchMapping(path = "/aprovar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(fotoMapper.toDto(fotoIService.aprovar(id)));
    }

    /**
     * Remove uma foto pelo ID.
     * DELETE /fotos/{id}
     * Requer: autenticação — RN13: só o dono ou admin pode deletar
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        fotoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}