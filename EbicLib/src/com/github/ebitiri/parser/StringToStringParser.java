package com.github.ebitiri.parser;

/**
 * StringをStringに変換。parseは引数をそのまま返すだけ。<br>
 * シングルトンクラス
 * @author ebi
 */
public class StringToStringParser implements Parser<String, String>{

	//シングルトン
	private StringToStringParser(){}
	private static final StringToStringParser INSTANCE = new StringToStringParser();
	public static StringToStringParser getInstance(){return INSTANCE;}
	
	@Override
	public String parse(String t){
		return t;
	}

	@Override
	public boolean tryParse(String t){
		return true;
	}
}
