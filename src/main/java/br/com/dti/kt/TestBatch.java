package br.com.dti.kt;

import static br.com.dti.kt.core.Constants.DEFAULT_PARALLELISM;
import static br.com.dti.kt.core.Constants.OUTPUT_PARALLELISM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;

import br.com.dti.kt.batch.output.DefautOutput;
import br.com.dti.kt.batch.source.DefautSource;
import br.com.dti.kt.core.common.ParameterWrapper;
import br.com.dti.kt.domain.dto.ComprasDto;

public class TestBatch {
 
	public static void main(String[] args) throws Exception {
		final ParameterTool parameter = ParameterWrapper.fromArgs(args);
	    final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
	
	    
	    DataSet<ComprasDto> result = DefautSource.getData(env, parameter).map(new MapFunction<Tuple5<BigInteger,String,BigDecimal,Date,Boolean>, ComprasDto>() {
			@Override
			public ComprasDto map(Tuple5<BigInteger, String, BigDecimal, Date, Boolean> value) throws Exception {
				return ComprasDto.builder()
						.codCompra(value.f0)
						.nomCliente(value.f1)
						.vlrCompra(value.f2)
						.datCompra(value.f3)
						.byInternet(value.f4)
						.build();
			}
		}).first(1000);
	    
	
	    int outputParalelism = parameter.has(OUTPUT_PARALLELISM) ? parameter.getInt(OUTPUT_PARALLELISM) : DEFAULT_PARALLELISM;
	    
	    result.output(DefautOutput.<ComprasDto>testFrom(parameter)).setParallelism(outputParalelism);        	
	    
	    env.execute(TestBatch.class.getSimpleName() + ' ' + new Date());
	}
}
