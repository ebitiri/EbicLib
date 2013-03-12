package com.github.ebitiri.parser;


/**
 * t, f, true, false の文字列を対応する真偽値に変換 （大文字小文字の区別はしない）
 * @author ebi
 */
public class StringToBoolParser implements Parser<String, Boolean>{

	//シングルトン
	private StringToBoolParser(){}
	private static final StringToBoolParser INSTANCE = new StringToBoolParser();
	public static StringToBoolParser getInstance(){return INSTANCE;}
	
	@Override
	public Boolean parse(String t){
		if(t.equalsIgnoreCase("t")) return true;
		if(t.equalsIgnoreCase("f")) return false;
		if(t.equalsIgnoreCase("true")) return true;
		if(t.equalsIgnoreCase("false")) return false;
		throw new IllegalArgumentException("this string cant parse to boolean");
	}

	@Override
	public boolean tryParse(String t){
		if(t.equalsIgnoreCase("t")) return true;
		if(t.equalsIgnoreCase("f")) return true;
		if(t.equalsIgnoreCase("true")) return true;
		if(t.equalsIgnoreCase("false")) return true;
		return false;
	}
}