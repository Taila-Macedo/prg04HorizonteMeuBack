package br.com.ifba.horizontemeu.pontoTuristico.repository;

import br.com.ifba.horizontemeu.pontoTuristico.entity.PontoTuristico;
import br.com.ifba.horizontemeu.pontoTuristico.enums.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PontoTuristicoRepository extends JpaRepository<PontoTuristico, Long> {

    List<PontoTuristico> findByNomeContainingIgnoreCase(String nome);

    List<PontoTuristico> findByPais(String pais);

    List<PontoTuristico> findByCategoria(Categoria categoria);

}
