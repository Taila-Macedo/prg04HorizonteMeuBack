package br.com.ifba.horizontemeu.roteiroponto.repository;

import br.com.ifba.horizontemeu.roteiroponto.entity.RoteiroNoPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoteiroNoPontoRepository extends JpaRepository<RoteiroNoPonto, Long> {
}