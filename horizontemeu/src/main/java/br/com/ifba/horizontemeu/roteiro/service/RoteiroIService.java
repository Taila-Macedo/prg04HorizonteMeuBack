package br.com.ifba.horizontemeu.roteiro.service;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroNoPontoRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import java.util.List;

public interface RoteiroIService {
    List<Roteiro> findByUsuario(Long idUsuario);
    Roteiro findById(Long id);
    Roteiro save(Roteiro roteiro, Long idUsuario, List<RoteiroNoPontoRequestDto> pontos);
    Roteiro update(Long id, Roteiro roteiro, List<RoteiroNoPontoRequestDto> pontos);
    void delete(Long id);
    void marcarComoVisitado(Long idRoteiroPonto, Boolean visitado);
    Roteiro compartilhar(Long id);
}