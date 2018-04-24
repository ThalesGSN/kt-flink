package br.com.dti.kt.domain.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprasDto {
	@JsonProperty("COD_COMPRA")
	private BigInteger codCompra;
	
	@JsonProperty("NOM_CLIENTE")
	private String nomCliente;
	
	@JsonProperty("VLR_COMPRA")
	private  BigDecimal vlrCompra;
	
	@JsonProperty("DAT_COMPRA")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date datCompra;
	
	@JsonProperty("BY_INTERNET")
	private Boolean byInternet;
}
