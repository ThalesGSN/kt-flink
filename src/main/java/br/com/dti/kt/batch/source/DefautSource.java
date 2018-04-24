package br.com.dti.kt.batch.source;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;

import br.com.dti.kt.core.common.MapperUtils;
import br.com.dti.kt.transformations.map.CsvMapper;
import br.com.dti.kt.transformations.map.RowMapper;

public class DefautSource {
	private DefautSource() { }
	
	public static DataSet<Tuple5<BigInteger, String, BigDecimal, Date, Boolean>> getData(ExecutionEnvironment env, ParameterTool parameter) {
		DataSet<Row> rowDataSet = env.createInput(DefautSource.from(parameter));
        
        
        DataSet<Tuple5<BigInteger, String, BigDecimal, Date, Boolean>> csvDataset = 
        		env.readCsvFile("file:///C:/resources/data.csv")
        			.ignoreFirstLine()
        			.ignoreInvalidLines()
        			.types(BigInteger.class, String.class, BigDecimal.class, String.class, Boolean.class)
        			.map(new CsvMapper());
        
        DataSet<Tuple5<BigInteger, String, BigDecimal, Date, Boolean>> jdbcDataset = rowDataSet.map(new RowMapper());
        
        
        
        return jdbcDataset.union(csvDataset);
	}
	
	public static JDBCInputFormat from(ParameterTool parameter) {        
	        
		return JDBCInputFormat.buildJDBCInputFormat()
	            .setDrivername(parameter.get("default.source.datasource.driver-class-name"))
	            .setDBUrl(parameter.get("default.source.datasource.url"))
	            .setUsername(parameter.get("default.source.datasource.username"))
	            .setPassword(parameter.get("default.source.datasource.password"))
	            .setRowTypeInfo(new RowTypeInfo(
		                BasicTypeInfo.BIG_INT_TYPE_INFO,
		                BasicTypeInfo.STRING_TYPE_INFO,
		                BasicTypeInfo.BIG_DEC_TYPE_INFO,
		                BasicTypeInfo.DATE_TYPE_INFO,
		                BasicTypeInfo.BOOLEAN_TYPE_INFO
		            ))
	            .setQuery("SELECT `COD_COMPRA`, `NOM_CLIENTE`, `VLR_COMPRA`, `DAT_COMPRA`, `BY_INTERNET` FROM `compras`")
	            .finish();
	}
}
	
