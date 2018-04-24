package br.com.dti.kt.core.connector.http;


import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

import java.io.IOException;

public class HttpOutputFormat<T> extends RichOutputFormat<T> {

    private HttpSink<T> httpSink;

    private Configuration configuration;

    public HttpOutputFormat(HttpSink<T> httpSink) {
        this.httpSink = httpSink;
    }

    @Override
    public void configure(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        httpSink.setRuntimeContext(getRuntimeContext());
        httpSink.open(configuration);
    }

    @Override
    public void writeRecord(T record) throws IOException {
        httpSink.invoke(record);
    }

    @Override
    public void close() throws IOException {
        httpSink.close();
    }
}
