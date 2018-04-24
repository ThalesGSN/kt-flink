package br.com.dti.kt.batch.output;

import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.utils.ParameterTool;

import br.com.dti.kt.core.connector.http.HttpOutputFormat;
import br.com.dti.kt.core.connector.http.HttpSink;
import br.com.dti.kt.domain.template.DefaultTemplate;

public class DefautOutput {
	
	private DefautOutput() { }
	
    public static <T> OutputFormat<T> from(ParameterTool parameter) {
	    	return new HttpOutputFormat<>(
	            HttpSink.<T>builder()
	                .url(parameter.get("default.output.http.properties.address.default"))
	                .template(DefaultTemplate.builder()
		                		.nome("Seu Nome aqui")
		                		.email("seu.email@dtidigital.com.br")
		                		.build())
	                .build()
	        );
    }
	    	
	public static <T> OutputFormat<T> testFrom(ParameterTool parameter) {
    	return new HttpOutputFormat<>(
            HttpSink.<T>builder()
                .url(parameter.get("default.output.http.properties.address.test"))
                .template(new DefaultTemplate())
                .build()
        );
    }
}