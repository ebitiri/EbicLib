package com.github.ebitiri.command;

import java.util.HashMap;





/**
 * CommandMethodsの集まり   
 *  /foo bar と /foo hoge  など、最初の文字列が同じコマンドを管理
 *  @author ebi
 */
public class CommandNameSpace{

	private HashMap<String, CommandObject> methods;
	
	/**
	 * 初期化
	 */
	public CommandNameSpace(){
		methods = new HashMap<String, CommandObject>();
	}
	
	/**
	 * コマンド登録
	 */
	public void addCommand(CommandObject cm){
		if(methods.containsKey(cm.getName())){
			throw new InvalidCommandException("同じ名前のコマンドが登録済みです。");
		}else{
			methods.put(cm.getName(), cm);
		}
	}
	
	/**
	 * コマンドメソッドの取得    ""を指定すると /foo などの一段式コマンドを取得する。
	 */
	public CommandObject get(String name){
		return methods.get(name);
	}
	
	/**
	 * 指定した名前のコマンドメソッドが存在するか
	 */
	public boolean contains(String name){
		return methods.containsKey(name);
	}
}















