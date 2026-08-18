package com.mitienda.tienda.shared;

public class AccesoDenegadoException extends RuntimeException{

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
