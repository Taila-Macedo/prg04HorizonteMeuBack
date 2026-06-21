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
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController implements FavoritoIController {

    private final FavoritoIService favoritoIService;
    private final FavoritoMapper favoritoMapper;

    /**
     * Lista todos os favoritos de um usuário.
     * GET /favoritos/usuario/{idUsuario}
     * Requer: autenticação
     */
    @Override
    @GetMapping(path = "/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                favoritoMapper.toGetResponseDtoList(
                        favoritoIService.findByUsuario(idUsuario)));
    }

    /**
     * Adiciona um ponto turístico aos favoritos do usuário.
     * POST /favoritos
     * Requer: autenticação
     * dataSalvo é preenchida automaticamente pelo service
     * Duplicata é verificada pelo service (RN03)
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid FavoritoPostRequestDto dto) {
        // FavoritoMapper converte o DTO para entidade com usuario.id e pontoTuristico.id
        // O service busca os objetos completos e valida a duplicata
        Favorito salvo = favoritoIService.save(favoritoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoritoMapper.toGetResponseDto(salvo));
    }

    /**
     * Remove um favorito pelo ID.
     * DELETE /favoritos/{id}
     * Requer: autenticação — RN13: só o dono pode remover o próprio favorito
     */
    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        favoritoIService.delete(id);
        return ResponseEntity.noContent().build();
    }
}