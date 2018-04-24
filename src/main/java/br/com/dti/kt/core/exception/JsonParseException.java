package br.com.dti.kt.core.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonParseException extends RuntimeException {

    public JsonParseException(JsonProcessingException e) {
        super(e);
    }
}
