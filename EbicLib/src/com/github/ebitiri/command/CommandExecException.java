package com.github.ebitiri.command;


/**
 * コマンドを実行した時の例外
 * @author ebi
 */
public class CommandExecException extends Exception{

	private static final long serialVersionUID = 7101050834999459709L;

	public CommandExecException(String msg){
		super(msg);
	}
}
