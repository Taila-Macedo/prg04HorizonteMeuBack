package br.com.ifba.horizontemeu.roteiro.service;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import java.util.List;

public interface RoteiroIService {
    List<Roteiro> findByUsuario(Long idUsuario);
    Roteiro findById(Long id);
    Roteiro save(RoteiroPostRequestDto dto);
    Roteiro update(Long id, RoteiroPostRequestDto dto);
    void delete(Long id);
    void marcarComoVisitado(Long idRoteiroPonto, Boolean visitado);
}
