package br.com.dti.kt.core.connector.http;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.flink.api.common.accumulators.Histogram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;

import br.com.dti.kt.core.common.JsonUtils;
import br.com.dti.kt.core.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class HttpSink<T> extends RichSinkFunction<T> {
    private static final long serialVersionUID = -2942271002715389999L;

    @Builder.Default
    private int bulkCount = 0;
    
    private transient CloseableHttpClient httpClient;
    private Histogram httpStatusesAccumulator;

    private String url;
    private HttpTemplate template;
 

    private transient List<T> values;

    @Override
    public void open(Configuration parameters) {
        httpClient = HttpClients.custom()
            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
            .build();
        httpStatusesAccumulator = getRuntimeContext().getHistogram("http_statuses");
    }

    @Override
    public synchronized void close() throws IOException {
	    	if(values != null && !values.isEmpty()) {
	            List<T> copyOfValues = new ArrayList<>(values);
	            values.clear();
	            executeBulk(copyOfValues, bulkCount);
	            bulkCount = 0;
	        }
	    	
        httpClient.close();
        httpStatusesAccumulator.resetLocal();
    	
    }

    @Override
    public void invoke(T value) throws IOException {
        if (values == null) {
            values = Collections.synchronizedList(new ArrayList<>());
        }
        values.add(value);
        bulkCount++;
        
        if (bulkCount >= 1000) {
            List<T> copyOfValues = new ArrayList<>(values);
            values.clear();
            executeBulk(copyOfValues, bulkCount);
            bulkCount = 0;
        }
    }

    private void executeBulk(List<T> values, int bulkCount) throws IOException {
        StringEntity requestEntity = new StringEntity(formatBody(values, bulkCount), "UTF-8");

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(requestEntity);
        httpPost.addHeader("Content-Type", "application/json");
        log.debug("HTTP POST: {}", requestEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int httpStatusCode = response.getStatusLine().getStatusCode();
            if (httpStatusCode < 200 || httpStatusCode >= 300) {
                throw new InvalidRequestException("An unexpected status code was received. Status " + httpStatusCode);
            }
            log.debug("HTTP STATUS CODE: {}", httpStatusCode);
            httpStatusesAccumulator.add(httpStatusCode);
            
        }
    }
    

    private String formatBody(List<T> values, int bulkCount) {
    	template.setQtdRegistros(bulkCount);
        template.setCodHash(BigInteger.valueOf(values.hashCode()));
    	template.setIndexes((List<Object>) values);
        
        return JsonUtils.toJson(template);
    }
    
    
}