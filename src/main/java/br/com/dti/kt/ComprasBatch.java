package br.com.dti.kt;

import static br.com.dti.kt.core.Constants.DEFAULT_PARALLELISM;
import static br.com.dti.kt.core.Constants.OUTPUT_PARALLELISM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;

import br.com.dti.kt.batch.source.DefautSource;
import br.com.dti.kt.core.common.ParameterWrapper;

public class ComprasBatch {

    private ComprasBatch() { }

    public static void main(String[] args) throws Exception {
        final ParameterTool parameter = ParameterWrapper.fromArgs(args);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<Tuple5<BigInteger, String, BigDecimal, Date, Boolean>> compraDataset = DefautSource.getData(env, parameter);

        int outputParalelism = parameter.has(OUTPUT_PARALLELISM) ? parameter.getInt(OUTPUT_PARALLELISM) : DEFAULT_PARALLELISM;
        
        //result.output(DefautOutput.<ComprasDto>from(parameter)).setParallelism(outputParalelism);        	
        
        env.execute(ComprasBatch.class.getSimpleName() + ' ' + new Date());
    }
}
