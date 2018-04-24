package br.com.dti.kt.core.common;

public class ParseUtils {
	private ParseUtils() { }

	public static Integer parseInt(String num){
		if(num != null) 
			return Integer.parseInt(num);
		
		return null;
	}
}
