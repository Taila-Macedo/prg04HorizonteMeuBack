package br.com.ifba.horizontemeu.foto.controller;

import br.com.ifba.horizontemeu.foto.dto.FotoGetResponseDto;
import br.com.ifba.horizontemeu.foto.dto.FotoPostRequestDto;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.foto.service.FotoIService;
import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
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
@RequestMapping(path = "/fotos")
@RequiredArgsConstructor
public class FotoController implements FotoIController{

    private final FotoIService fotoIService;

    // coloca o ObjectMapperUtil para converter entidades em DTOs e vice-versa
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todas as fotos paginadas.
     * GET /fotos/findall
     */
    @Override
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<FotoGetResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fotoIService.findAll(pageable)
                        .map(f -> objectMapperUtil.map(f, FotoGetResponseDto.class)));
    }

    /**
     * Busca foto por ID.
     * GET /fotos/findbyid/{id}
     * Se não encontrar retorna 404
     */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return fotoIService.findById(id)
                .<ResponseEntity<?>>map(f -> ResponseEntity.ok(
                        objectMapperUtil.map(f, FotoGetResponseDto.class)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Foto não encontrada com id: " + id));
    }

    /**
     * Busca todas as fotos de um ponto turístico.
     * GET /fotos/findbyponto/{idPonto}
     */
    @Override
    @GetMapping(path = "/findbyponto/{idPonto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPontoTuristico(@PathVariable Long idPonto) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                fotoIService.findByPontoTuristico(idPonto),
                FotoGetResponseDto.class));
    }

    /**
     * Busca fotos aprovadas ou pendentes.
     * GET /fotos/findbyaprovado?aprovado=false
     */
    @Override
    @GetMapping(path = "/findbyaprovado", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByAprovado(@RequestParam Boolean aprovado) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                fotoIService.findByAprovado(aprovado),
                FotoGetResponseDto.class));
    }

    /**
     * Salva uma nova foto — aprovado começa como false.
     * POST /fotos/save
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid FotoPostRequestDto dto) {
        Foto foto = new Foto();
        foto.setUrl(dto.getUrl());
        foto.setLegenda(dto.getLegenda());

        // cria objetos com só o ID para o Service buscar e validar
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        foto.setUsuario(usuario);

        PontoTuristico ponto = new PontoTuristico();
        ponto.setId(dto.getIdPontoTuristico());
        foto.setPontoTuristico(ponto);

        Foto salva = fotoIService.save(foto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salva, FotoGetResponseDto.class));
    }

    /**
     * Admin aprova a foto para aparecer na galeria.
     * PATCH /fotos/aprovar/{id}
     */
    @Override
    @PatchMapping(path = "/aprovar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        Foto aprovada = fotoIService.aprovar(id);
        return ResponseEntity.ok(
                objectMapperUtil.map(aprovada, FotoGetResponseDto.class));
    }

    /**
     * Remove uma foto pelo ID.
     * DELETE /fotos/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        fotoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
