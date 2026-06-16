package br.com.ifba.horizontemeu.roteiro.controller;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import br.com.ifba.horizontemeu.roteiro.service.RoteiroIService;
import br.com.ifba.horizontemeu.roteiro.mapper.RoteiroMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/roteiros")
@RequiredArgsConstructor
public class RoteiroController implements RoteiroIController {

    private final RoteiroIService roteiroIService;
    private final RoteiroMapper roteiroMapper;

    /** GET /roteiros/findbyusuario/{idUsuario} */
    @Override
    @GetMapping(path = "/findbyusuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDtoList(roteiroIService.findByUsuario(idUsuario))
        );
    }

    /** GET /roteiros/findbyid/{id} */
    @Override
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.findById(id))
        );
    }

    /** POST /roteiros/save */
    @Override
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody @Valid RoteiroPostRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roteiroMapper.toGetResponseDto(roteiroIService.save(dto)));
    }

    /** PUT /roteiros/update/{id} */
    @Override
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid RoteiroPostRequestDto dto) {
        return ResponseEntity.ok(
                roteiroMapper.toGetResponseDto(roteiroIService.update(id, dto))
        );
    }

    /** DELETE /roteiros/delete/{id} */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roteiroIService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** PATCH /roteiros/marcar-visitado/{idRoteiroPonto}?visitado=true */
    @Override
    @PatchMapping(path = "/marcar-visitado/{idRoteiroPonto}")
    public ResponseEntity<?> marcarComoVisitado(
            @PathVariable Long idRoteiroPonto,
            @RequestParam Boolean visitado) {

        roteiroIService.marcarComoVisitado(idRoteiroPonto, visitado);
        return ResponseEntity.ok().body("Status de visita atualizado com sucesso!");
    }
}