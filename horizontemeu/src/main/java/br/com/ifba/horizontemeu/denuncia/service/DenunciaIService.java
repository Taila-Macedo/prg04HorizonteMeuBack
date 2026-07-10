package br.com.ifba.horizontemeu.denuncia.service;

import br.com.ifba.horizontemeu.denuncia.entity.Denuncia;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DenunciaIService {

    Page<Denuncia> findAll(Pageable pageable);
    Optional<Denuncia> findById(Long id);
    List<Denuncia> findByUsuario(Long idUsuario);
    Page<Denuncia> findByStatus(StatusDenuncia status, Pageable pageable);
    Denuncia enviar(Denuncia denuncia);
    Denuncia resolver(Long id);
    Denuncia rejeitar(Long id);
    Denuncia resolverExcluindoConteudo(Long id);
    void delete(Long id);
}