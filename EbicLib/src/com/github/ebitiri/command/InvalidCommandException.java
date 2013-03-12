package com.github.ebitiri.command;


/**
 * 無効なコマンドが登録されたり、実行されたりした場合に発生する。
 * 実行時例外。
 * @author ebi
 */
public class InvalidCommandException extends RuntimeException{

	private static final long serialVersionUID = -4870775947818947112L;

	public InvalidCommandException(String msg){
		super(msg);
	}
}
