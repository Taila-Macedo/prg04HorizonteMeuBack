package br.com.ifba.horizontemeu.infrastructure.exception;

// Exceção personalizada para erros de regra de negócio
// Estende RuntimeException para não precisar declarar no throws
public class BusinessException extends RuntimeException {

    //Construtor que recebe a mensagem de erro
    public BusinessException(String message) {
        super(message);
    }
}
