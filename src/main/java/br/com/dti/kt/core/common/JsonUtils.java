package br.com.dti.kt.core.common;

import br.com.dti.kt.core.exception.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.TimeZone;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(Object object) {
        mapper.setTimeZone(TimeZone.getDefault());
        
        
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Json mapping error", e);
            throw new JsonParseException(e);
        }
    }
}
