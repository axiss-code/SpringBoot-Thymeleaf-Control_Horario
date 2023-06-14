package com.ilerna.wr.exception;

//Tipo de excepcion personalizada para que se produzca cuando no se encuentra un valor en BD
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2047045858393072648L;

    public ResourceNotFoundException(Long itemNotFoundException) {
        super("Elemento no encontrado: " + itemNotFoundException);
    }
}
