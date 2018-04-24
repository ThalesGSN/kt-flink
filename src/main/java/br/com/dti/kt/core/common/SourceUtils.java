package br.com.dti.kt.core.common;

import java.io.Serializable;

public class SourceUtils {

    private SourceUtils() {

    }

    public static Serializable[][] getParameters(String... params) {
        Serializable[][] queryParameters = new String[1][params.length];
        queryParameters[0] = params;
        return queryParameters;
    }

}
