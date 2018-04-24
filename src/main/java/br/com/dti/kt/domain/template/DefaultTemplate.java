package br.com.dti.kt.domain.template;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.dti.kt.core.connector.http.HttpTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultTemplate implements HttpTemplate,  Serializable {
    private static final long serialVersionUID = 6435381176070317987L;
    
    @JsonProperty("NOME")
    private String nome;

    @JsonProperty("EMAIL")
    private String email;
    
    @JsonProperty("qtdRegistros")
    private Integer qtdRegistros;

    @JsonProperty("data")
    private transient List<Object> indexes;
    
    @JsonProperty("hash")
    private BigInteger codHash;
    
    
}
