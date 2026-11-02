package com.gtvnv.designpatterns.exception;

/**
 * Lancada quando um recurso solicitado nao existe. Mapeada para HTTP 404
 * pelo {@link GlobalExceptionHandler}.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
