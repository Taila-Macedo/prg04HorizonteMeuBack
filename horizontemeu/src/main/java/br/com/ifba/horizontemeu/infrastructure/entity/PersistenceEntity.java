package br.com.ifba.horizontemeu.infrastructure.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe base para todas as entidades do sistema.
 * Define o ID gerado automaticamente pelo banco de dados.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class PersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}