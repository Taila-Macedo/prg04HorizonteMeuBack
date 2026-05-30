package br.com.ifba.horizontemeu.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Classe que representa o formato padronizado do erro retornado ao cliente
public class ErrorResponse {

    //código http do erro
    private int status;

    //mensagem descritiva do erro
    private String message;

    // campos extras — só preenchidos em erros de validação
    private String fields;        // ex: "nome, email"
    private String fieldsMessage; // ex: "não pode ser vazio!, e-mail inválido!"
}
