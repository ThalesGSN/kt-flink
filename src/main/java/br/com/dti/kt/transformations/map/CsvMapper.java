package br.com.dti.kt.transformations.map;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple5;

public class CsvMapper implements MapFunction<Tuple5<BigInteger,String,BigDecimal,String,Boolean>, Tuple5<BigInteger,String,BigDecimal,Date,Boolean>> {
		private static final long serialVersionUID = 7876735664962814203L;

		@Override
		public Tuple5<BigInteger, String, BigDecimal, Date, Boolean> map(
				Tuple5<BigInteger, String, BigDecimal, String, Boolean> value) throws Exception {
			return new Tuple5<>(
					value.f0,
					value.f1, 
					value.f2, 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.f3),
					value.f4
				);
		}
	
}
