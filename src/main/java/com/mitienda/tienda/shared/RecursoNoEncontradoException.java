package com.mitienda.tienda.shared;

public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
