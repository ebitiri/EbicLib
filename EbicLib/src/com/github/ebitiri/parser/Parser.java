package com.github.ebitiri.parser;


/**
 * 変換クラス
 * @author ebi
 */
public interface Parser<T, S>{

	/**
	 * T型の値をS型に変換
	 */
	public S parse(T t);
	
	/**
	 * parse(t)が可能かどうか
	 */
	public boolean tryParse(T t);
}
