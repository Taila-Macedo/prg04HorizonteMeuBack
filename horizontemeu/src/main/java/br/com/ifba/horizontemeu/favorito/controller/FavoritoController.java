package br.com.ifba.horizontemeu.favorito.controller;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoGetResponseDto;
import br.com.ifba.horizontemeu.favorito.dto.FavoritoPostRequestDto;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import br.com.ifba.horizontemeu.favorito.service.FavoritoIService;
import br.com.ifba.horizontemeu.infrastructure.mapper.ObjectMapperUtil;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/favoritos")
@RequiredArgsConstructor
public class FavoritoController implements FavoritoIController{

    private final FavoritoIService favoritoIService;
    private final ObjectMapperUtil objectMapperUtil;

    /**
     * Lista todos os favoritos de um usuário.
     * GET /favoritos/findbyusuario/{idUsuario}
     */
    @Override
    @GetMapping(path = "/findbyusuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(objectMapperUtil.mapAll(
                favoritoIService.findByUsuario(idUsuario),
                FavoritoGetResponseDto.class));
    }

    /**
     * Adiciona um ponto turístico aos favoritos.
     * POST /favoritos/save
     */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid FavoritoPostRequestDto dto) {
        Favorito favorito = new Favorito();

        // cria objetos com só o ID para o Service buscar e validar
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        favorito.setUsuario(usuario);

        PontoTuristico ponto = new PontoTuristico();
        ponto.setId(dto.getIdPontoTuristico());
        favorito.setPontoTuristico(ponto);

        Favorito salvo = favoritoIService.save(favorito);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objectMapperUtil.map(salvo, FavoritoGetResponseDto.class));
    }

    /**
     * Remove um favorito pelo ID.
     * DELETE /favoritos/delete/{id}
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        favoritoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
