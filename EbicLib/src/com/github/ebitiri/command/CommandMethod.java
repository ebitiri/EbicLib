package com.github.ebitiri.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.github.ebitiri.parser.Parser;


/**
 * CommandPropertyアノテーションが付いたメソッドを管理
 * @author ebi
 */
public class CommandMethod implements CommandObject{

	private Method method;
	private Object instance;
	private ArrayList<Parser<String, ?>> parsers;
	private boolean playerOnly;
	private CommandProperty property;
	
	/**
	 * 初期化
	 */
	CommandMethod(Method m, Object ins, ArrayList<Parser<String, ?>> ps, boolean po, CommandProperty p){
		this.method = m;
		this.instance = ins;
		this.parsers = ps;
		this.playerOnly = po;
		this.property = p;
	}
	
	/**
	 * コマンドを実行
	 */
	public void exec(CommandSender sender, String[] args) throws CommandExecException{
		//パラメータ数の確認
		if(args.length != parsers.size()){
			throw new CommandExecException("引数が間違っています。");
		}
		
		//パラメータ配列
		Object[] params = new Object[parsers.size()+1];
		
		//最初の引数は送信者
		params[0] = sender;
		
		//以降の引数は変換して入れる
		for(int i = 0; i < parsers.size(); i++){
			//変換可能？
			if(parsers.get(i).tryParse(args[i])){
				//変換
				params[i + 1] = parsers.get(i).parse(args[i]);
			}
			//変換不可
			else{
				throw new CommandExecException("引数が間違っています。");
			}
		}
		
		//呼び出し
		try{
			method.invoke(instance, params);
		}catch(IllegalArgumentException e){
			throw new CommandExecException("引数が間違っています。");
		}catch(IllegalAccessException e){
			throw new InvalidCommandException("コマンドの呼び出しに失敗しました");
		}catch(InvocationTargetException e){
			throw new InvalidCommandException("コマンドの呼び出しに失敗しました");
		}
	}
	
	public boolean isPlayerCommand(){return playerOnly;}
	public String getPerm(){return property.perm();}
	public String getName(){return property.name();}
	public String getNameSpace(){return property.namespace();}
}
