package br.com.dti.kt.transformations.map;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.types.Row;

import br.com.dti.kt.core.common.MapperUtils;

public class RowMapper implements MapFunction<Row, Tuple5<BigInteger, String, BigDecimal, Date, Boolean>> {

	private static final long serialVersionUID = 3654042442141381057L;

	@Override
	public Tuple5<BigInteger, String, BigDecimal, Date, Boolean> map(Row value) throws Exception {
		return new Tuple5<>(
				MapperUtils.convert(value.getField(0), BigInteger.class),
				MapperUtils.convert(value.getField(1), String.class),
				MapperUtils.convert(value.getField(2), BigDecimal.class),
				MapperUtils.parseDate((String)value.getField(3)), 
				MapperUtils.convert(value.getField(4), Boolean.class)
			);

	}
}
