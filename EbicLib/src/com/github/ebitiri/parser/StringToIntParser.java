package com.github.ebitiri.parser;



/**
 * StringをIntに変換。Integer.parseIntと同じ仕様。
 * @author ebi
 */
public class StringToIntParser implements Parser<String, Integer>{

	//シングルトン
	private StringToIntParser(){}
	private static final StringToIntParser INSTANCE = new StringToIntParser();
	public static StringToIntParser getInstance(){return INSTANCE;}
	
	@Override
	public Integer parse(String t){
		return Integer.parseInt(t);
	}

	@Override
	public boolean tryParse(String t){
		return t.matches("^-?[0-9]+$");
	}
}
