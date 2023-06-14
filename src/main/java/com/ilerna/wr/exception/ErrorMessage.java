package com.ilerna.wr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
 * Permite personalizar los mensajes de error enviados a /error -> error.html
*/
@ToString
@Getter
@AllArgsConstructor
public class ErrorMessage {
    
    private String code;
    private String name;
    private String message;
    private String url;

}
