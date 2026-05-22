package br.com.ifba.horizontemeu.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Classe que representa o formato padronizado do erro retornado ao cliente
public class ErrorResponse {

    //código http do erro
    private int status;

    //mensagem descritiva do erro
    private String message;
}
