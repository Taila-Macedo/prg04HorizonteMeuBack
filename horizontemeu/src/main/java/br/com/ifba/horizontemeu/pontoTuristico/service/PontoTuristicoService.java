package br.com.ifba.horizontemeu.pontoTuristico.service;

import br.com.ifba.horizontemeu.infrastructure.exception.BusinessException;
import br.com.ifba.horizontemeu.pontoTuristico.dto.PontoTuristicoPutRequestDto;
import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.repository.PontoTuristicoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PontoTuristicoService implements PontoTuristicoIService {

    private final PontoTuristicoRepository pontoTuristicoRepository;
    private static final Logger log = LoggerFactory.getLogger(PontoTuristicoService.class);


    @Override
    public Page<PontoTuristico> findAll(Pageable pageable) {
        log.info("Buscando todos os pontos turísticos...");
        return pontoTuristicoRepository.findAll(pageable);
    }

    @Override
    public Optional<PontoTuristico> findById(Long id) {
        log.info("buscando ponto turístico com id: {}", id);
        return pontoTuristicoRepository.findById(id);
    }

    @Transactional
    @Override
    public PontoTuristico save(PontoTuristico ponto) {
        if(ponto == null) {
            throw new BusinessException("Dados do ponto turístico não preenchidos.");
        } if(ponto.getId() != null) {
            throw new BusinessException("Ponto turístico já existente. Use o update.");
        }

        //Verificar se já existe um ponto com o mesmo nome para evitar duplicatas
        if (pontoTuristicoRepository.findByNomeContainingIgnoreCase(ponto.getNome())
                .stream().anyMatch(p -> p.getNome().equalsIgnoreCase(ponto.getNome()))) {
            throw new BusinessException("Já existe um ponto turístico com o nome: " + ponto.getNome());
        }

        //nota_media começa como 0.0 e é recalculada quando comentários forem publicados
        ponto.setNotaMedia(0.0f);

        log.info("salvando novo ponto turístico: {}", ponto.getNome());
        return pontoTuristicoRepository.save(ponto);
    }

    @Transactional
    @Override
    public PontoTuristico update(Long id, PontoTuristicoPutRequestDto dto) {
        PontoTuristico existente = pontoTuristicoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ponto turístico não encontrado com id: " + id));

        // Atualiza todos os campos editáveis
        // notaMedia NÃO é atualizada aqui — é recalculada automaticamente (RN04)
        existente.setNome(dto.getNome());
        existente.setDescricao(dto.getDescricao());
        existente.setCidade(dto.getCidade());
        existente.setPais(dto.getPais());
        existente.setLatitude(dto.getLatitude());
        existente.setLongitude(dto.getLongitude());
        existente.setCategoria(dto.getCategoria());
        existente.setNoMapa3D(dto.getNoMapa3D() != null ? dto.getNoMapa3D() : false);

        log.info("Atualizando ponto turístico id: {}", id);
        return pontoTuristicoRepository.save(existente);
    }

    @Transactional
    @Override
    public void delete(long id) {
        if(!pontoTuristicoRepository.existsById(id)){
            throw new BusinessException("Ponto turístico não encontrado com id: " + id);
        }

        log.info("Removendo ponto turístico id: {}", id);
        pontoTuristicoRepository.deleteById(id);

    }

    @Override
    public List<PontoTuristico> findByNome(String nome) {
        log.info("buscando ponto turístico pelo nome: {}", nome);
        return pontoTuristicoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public List<PontoTuristico> findByPais(String pais) {
        log.info("Buscando pontos turísticos pelo país: {}", pais);
        return pontoTuristicoRepository.findByPais(pais);
    }
}
