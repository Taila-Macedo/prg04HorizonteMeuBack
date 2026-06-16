package br.com.ifba.horizontemeu.roteiro.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.repository.PontoTuristicoRepository;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroNoPontoRequestDto;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import br.com.ifba.horizontemeu.roteiro.mapper.RoteiroMapper;
import br.com.ifba.horizontemeu.roteiro.repository.RoteiroRepository;
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

    private final RoteiroMapper roteiroMapper;

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
    public Roteiro save(RoteiroPostRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + dto.getIdUsuario()));

        //Converte o DTO para entidade (sem pontos ainda)
        Roteiro roteiro = roteiroMapper.toEntity(dto);
        roteiro.setUsuario(usuario);
        roteiro.setDataCriacao(LocalDate.now());

        // Monta os RoteiroNoPonto e associa ao Roteiro
        // O cascade ALL vai salvar os pontos automaticamente junto com o roteiro
        if (dto.getPontos() != null && !dto.getPontos().isEmpty()) {
            List<RoteiroNoPonto> pontos = montarPontos(dto.getPontos(), roteiro);
            roteiro.getPontos().addAll(pontos);
        }

        log.info("Salvando roteiro '{}' para o usuário {}", roteiro.getTitulo(), usuario.getEmail());
        return roteiroRepository.save(roteiro);
    }

    @Transactional
    @Override
    public Roteiro update(Long id, RoteiroPostRequestDto dto) {
        Roteiro roteiro = roteiroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Roteiro não encontrado com id: " + id));


        roteiro.setTitulo(dto.getTitulo());
        roteiro.setDescricao(dto.getDescricao());
        roteiro.setDataViagem(dto.getDataViagem());
        roteiro.setPublico(dto.getPublico() != null ? dto.getPublico() : false);


        roteiro.getPontos().clear();

        if (dto.getPontos() != null && !dto.getPontos().isEmpty()) {
            List<RoteiroNoPonto> novosPontos = montarPontos(dto.getPontos(), roteiro);
            roteiro.getPontos().addAll(novosPontos);
        }

        log.info("Atualizando roteiro id: {}", id);
        return roteiroRepository.save(roteiro);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!roteiroRepository.existsById(id)) {
            throw new BusinessException("Roteiro não encontrado com id: " + id);
        }
        // O cascade ALL + orphanRemoval deleta os RoteiroNoPonto junto
        log.info("Removendo roteiro id: {}", id);
        roteiroRepository.deleteById(id);
    }

    // Método auxiliar: monta a lista de RoteiroNoPonto a partir dos DTOs
    private List<RoteiroNoPonto> montarPontos(List<RoteiroNoPontoRequestDto> dtos, Roteiro roteiro) {

        List<RoteiroNoPonto> pontos = new ArrayList<>();

        for (RoteiroNoPontoRequestDto pontoDto : dtos) {
            PontoTuristico ponto = pontoTuristicoRepository.findById(pontoDto.getIdPontoTuristico())
                    .orElseThrow(() -> new BusinessException(
                            "Ponto turístico não encontrado com id: " + pontoDto.getIdPontoTuristico()));

            RoteiroNoPonto roteiroNoPonto = new RoteiroNoPonto();
            roteiroNoPonto.setRoteiro(roteiro);       // associa de volta ao pai
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

        // Atualiza apenas o status do checklist
        vinculo.setVisitado(visitado != null ? visitado : false);

        log.info("Ponto turístico do vínculo id {} marcado como visitado: {}", idRoteiroPonto, vinculo.getVisitado());
        roteiroNoPontoRepository.save(vinculo);
    }
}