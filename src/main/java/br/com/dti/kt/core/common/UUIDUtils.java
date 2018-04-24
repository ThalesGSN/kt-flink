package br.com.dti.kt.core.common;

import java.util.UUID;

import org.apache.flink.api.java.utils.ParameterTool;

public class UUIDUtils {
	private UUIDUtils(){ }
	
	public static String generateUUID(ParameterTool parameter){
		if(!parameter.has("default.output.properties.isTest")) {
			return UUID.randomUUID().toString();
		}	else {
			return "YYY";
		}
	}
}
