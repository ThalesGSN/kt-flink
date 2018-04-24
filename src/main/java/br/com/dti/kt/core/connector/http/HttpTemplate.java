package br.com.dti.kt.core.connector.http;

import java.io.Serializable;
import java.util.List;

public interface HttpTemplate extends Serializable {

    List<Object> getIndexes();

    void setIndexes(List<Object> indexes);
    
    Integer getQtdRegistros();

    void setQtdRegistros(Integer qtdRegistros);
}
