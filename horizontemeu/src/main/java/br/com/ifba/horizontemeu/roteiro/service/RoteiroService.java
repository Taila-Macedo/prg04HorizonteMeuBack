package br.com.ifba.horizontemeu.roteiro.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.repository.PontoTuristicoRepository;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroNoPontoRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import br.com.ifba.horizontemeu.roteiro.repository.RoteiroRepository;
// ALTERADO: removidos os imports de RoteiroPostRequestDto, RoteiroPutRequestDto e RoteiroMapper
// — o service não converte mais DTO, então não precisa conhecer essas classes
import br.com.ifba.horizontemeu.roteiroponto.entity.RoteiroNoPonto;
import br.com.ifba.horizontemeu.roteiroponto.repository.RoteiroNoPontoRepository;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import br.com.ifba.horizontemeu.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoteiroService implements RoteiroIService {

    private final RoteiroRepository roteiroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;
    private final RoteiroNoPontoRepository roteiroNoPontoRepository;
    // ALTERADO: removido "private final RoteiroMapper roteiroMapper;" — o service não usa mapper

    private static final Logger log = LoggerFactory.getLogger(RoteiroService.class);

    @Override
    public List<Roteiro> findByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + idUsuario));
        log.info("Buscando roteiros do usuário id: {}", idUsuario);
        return roteiroRepository.findByUsuario(usuario);
    }

    @Override
    public Roteiro findById(Long id) {
        return roteiroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Roteiro não encontrado com id: " + id));
    }

    @Transactional
    @Override
    public Roteiro save(Roteiro roteiro, Long idUsuario, List<RoteiroNoPontoRequestDto> pontosDto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + idUsuario));

        // ALTERADO: removida a linha "Roteiro roteiro = roteiroMapper.toEntity(dto);"
        // — o roteiro já chega pronto como parâmetro
        roteiro.setUsuario(usuario);
        roteiro.setDataCriacao(LocalDate.now());

        if (pontosDto != null && !pontosDto.isEmpty()) {
            List<RoteiroNoPonto> pontos = montarPontos(pontosDto, roteiro);
            roteiro.getPontos().addAll(pontos);
        }

        log.info("Salvando roteiro '{}' para o usuário {}", roteiro.getTitulo(), usuario.getEmail());
        return roteiroRepository.save(roteiro);
    }

    @Transactional
    @Override
    public Roteiro update(Long id, Roteiro roteiro, List<RoteiroNoPontoRequestDto> pontosDto) {
        Roteiro existente = roteiroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Roteiro não encontrado com id: " + id));

        // ALTERADO: antes lia "dto.getTitulo()" etc. — agora lê do objeto "roteiro" (entidade) recebido
        existente.setTitulo(roteiro.getTitulo());
        existente.setDescricao(roteiro.getDescricao());
        existente.setDataViagem(roteiro.getDataViagem());
        existente.setPublico(roteiro.getPublico() != null ? roteiro.getPublico() : false);

        existente.getPontos().clear();
        if (pontosDto != null && !pontosDto.isEmpty()) {
            List<RoteiroNoPonto> novosPontos = montarPontos(pontosDto, existente);
            existente.getPontos().addAll(novosPontos);
        }

        log.info("Atualizando roteiro id: {}", id);
        return roteiroRepository.save(existente);
    }


    @Transactional
    @Override
    public void delete(Long id) {
        if (!roteiroRepository.existsById(id)) {
            throw new BusinessException("Roteiro não encontrado com id: " + id);
        }
        log.info("Removendo roteiro id: {}", id);
        roteiroRepository.deleteById(id);
    }

    private List<RoteiroNoPonto> montarPontos(List<RoteiroNoPontoRequestDto> dtos, Roteiro roteiro) {
        List<RoteiroNoPonto> pontos = new ArrayList<>();
        for (RoteiroNoPontoRequestDto pontoDto : dtos) {
            PontoTuristico ponto = pontoTuristicoRepository.findById(pontoDto.getIdPontoTuristico())
                    .orElseThrow(() -> new BusinessException(
                            "Ponto turístico não encontrado com id: " + pontoDto.getIdPontoTuristico()));

            RoteiroNoPonto roteiroNoPonto = new RoteiroNoPonto();
            roteiroNoPonto.setRoteiro(roteiro);
            roteiroNoPonto.setPontoTuristico(ponto);
            roteiroNoPonto.setOrdem(pontoDto.getOrdem());
            roteiroNoPonto.setVisitado(false);
            pontos.add(roteiroNoPonto);
        }
        return pontos;
    }

    @Transactional
    @Override
    public void marcarComoVisitado(Long idRoteiroPonto, Boolean visitado) {
        RoteiroNoPonto vinculo = roteiroNoPontoRepository.findById(idRoteiroPonto)
                .orElseThrow(() -> new BusinessException("Vínculo de roteiro e ponto não encontrado com id: " + idRoteiroPonto));

        vinculo.setVisitado(visitado != null ? visitado : false);

        log.info("Ponto turístico do vínculo id {} marcado como visitado: {}", idRoteiroPonto, vinculo.getVisitado());
        roteiroNoPontoRepository.save(vinculo);
    }

    @Transactional
    @Override
    public Roteiro compartilhar(Long id) {
        Roteiro roteiro = roteiroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Roteiro não encontrado com id: " + id));

        roteiro.setPublico(true);

        log.info("Roteiro id {} compartilhado publicamente.", id);
        return roteiroRepository.save(roteiro);
    }
}