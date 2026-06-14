package br.com.ifba.horizontemeu.favorito.controller;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoPostRequestDto;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import br.com.ifba.horizontemeu.favorito.mapper.FavoritoMapper;
import br.com.ifba.horizontemeu.favorito.service.FavoritoIService;
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
    private final FavoritoMapper favoritoMapper;

    /**
     * Lista todos os favoritos de um usuário.
     * GET /favoritos/findbyusuario/{idUsuario}
     */
    @Override
    @GetMapping(path = "/findbyusuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        // Usa o MapStruct (favoritoMapper) para converter a lista de Entidades para lista de DTOs
        return ResponseEntity.ok(favoritoMapper.toGetResponseDtoList(
                favoritoIService.findByUsuario(idUsuario)
        ));
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

        // O MapStruct converte o DTO para a Entidade, criando a "casca" do Usuario e PontoTuristico sozinho
        Favorito favorito = favoritoMapper.toEntity(dto);

        Favorito salvo = favoritoIService.save(favorito);

        // Retorna o DTO convertido pelo MapStruct
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoritoMapper.toGetResponseDto(salvo));
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