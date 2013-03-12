package com.github.ebitiri.command;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ebitiri.parser.Parser;
import com.github.ebitiri.parser.StringToBoolParser;
import com.github.ebitiri.parser.StringToIntParser;
import com.github.ebitiri.parser.StringToStringParser;


/**
 * <pre>
 * コマンド管理クラス
 * {@link #registerCommand(CommandObject)}や{@link #registerCommandsOf(Object)}
 * で登録されたコマンドを管理する。
 * これらのコマンドはコンストラクタ引数に指定したプラグインのコマンドとして扱われる。
 * 内部で{@link PluginCommand#setExecutor(CommandExecutor)}を使って処理を受け持っているため、
 * 登録したコマンドは自動的に実行される。
 * </pre>
 * @author ebi
 */
public class CommandManager{

	private HashMap<String, CommandNameSpace> commands;
	private JavaPlugin plugin;
	private CommandExecutor cmdExecutor;
	private HashMap<Class<?>, Parser<String, ?>> parsers;
	
	/**
	 * 初期化
	 */
	public CommandManager(JavaPlugin plugin){
		this.commands = new HashMap<String, CommandNameSpace>();
		this.plugin = plugin;
		this.cmdExecutor = new CmdExecDelegate();
		this.parsers = new HashMap<Class<?>, Parser<String, ?>>();
		
		//基本的なパーサーを登録
		registerParser(StringToStringParser.getInstance(), String.class);
		registerParser(StringToIntParser.getInstance(), int.class);
		registerParser(StringToBoolParser.getInstance(), boolean.class);
	}
	
	/**
	 * 引数を変換するのに用いるパーサーを登録。　同じ型に対するパーサーが既に登録されている場合上書き。<br>
	 * String,int,booleanについては登録済み<br>
	 * String引数は何も処理をしない<br>
	 * Int引数はInteger.parseIntを利用<br>
	 * Bool引数はt,f,true,falseを大文字小文字の区別なく変換
	 */
	public <T> void registerParser(Parser<String, T> parser, Class<T> clazz){
		parsers.put(clazz, parser);
	}
	
	/**
	 * コマンドを実行
	 * @return コマンドが見つかったか
	 */
	public boolean execCommand(CommandSender sender, Command cmd, String[] args){
		//
		CommandNameSpace cns = commands.get(cmd.getName());
		if(cns == null) return false;
		
		CommandObject co = null;
		String[] newArgs = args;
		
		//ニ段式コマンド？
		if(args.length != 0 && cns.contains(args[0])){
			//引数変換
			newArgs = new String[args.length - 1];
			for(int i = 0; i < newArgs.length; i++){
				newArgs[i] = args[i + 1];
			}
			//取得
			co = cns.get(args[0]);
		}
		//一段式？
		else if(cns.contains("")){
			//取得
			co = cns.get("");
		}
		//見つからない
		else{
			return false;
		}
		
		//チェック
		if(co.isPlayerCommand() && !(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーからのみ実行可能です。");
			return true;
		}
		if(!co.getPerm().isEmpty() && !sender.hasPermission(co.getPerm())){
			sender.sendMessage(ChatColor.RED + "このコマンドを実行するのに必要なパーミッションがありません。");
			return true;
		}
		
		//実行
		try{
			co.exec(sender, newArgs);
		}catch(CommandExecException e){
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		//例外が出ていても、コマンド自体が存在することに変わりはない
		return true;
	}

	/**
	 * 指定したインスタンスに含まれるCommandPropertyアノテーションの付加されたメソッドをコマンドとして登録する。<br>
	 * メソッドの定義方法については{@link CommandProperty}を参照。
	 */
	public void registerCommandsOf(Object obj){
		//インスタンスがないと登録不可
		if(obj == null) throw new InvalidCommandException("実体のないコマンドは登録できません。");
		//objに含まれるメソッドでループ
		for(Method met : obj.getClass().getMethods()){
			registerMethod(met, obj);
		}
	}
	
	/**
	 * コマンドを登録する。<br>
	 * コマンドの名前空間はplugin.ymlで定義されたコマンドの名前である必要がある。<br>
	 * また、同じ名前空間に同じ名前のコマンドを２つ定義することはできない。（実行時例外）
	 */
	public void registerCommand(CommandObject cmd){
		//名前空間が登録されていなければ作成し登録
		if(!commands.containsKey(cmd.getName())){
			commands.put(cmd.getNameSpace(), new CommandNameSpace());
		}
		//追加
		commands.get(cmd.getNameSpace()).addCommand(cmd);
		//処理を移譲
		PluginCommand bCommand = plugin.getCommand(cmd.getNameSpace());
		if(bCommand == null){
			throw new InvalidCommandException("plugin.ymlで定義されていないコマンドです");
		}
		bCommand.setExecutor(cmdExecutor);
	}
	
	//以下private
	
	/** 
	 * 指定されたメソッドをコマンドとして登録する。
	 */
	private void registerMethod(Method met, Object obj){
		//アクセスできなければ対象外
		if(!Modifier.isPublic(met.getModifiers())) return;
		//staticなら対象外
		if(Modifier.isStatic(met.getModifiers())) return;
		
		//Command情報を取得
		CommandProperty prop = null;
		for(Annotation an : met.getAnnotations()){
			if(an instanceof CommandProperty){
				prop = (CommandProperty)an;
			}
		}
		if(prop == null) return;
	
		//パラメータ型配列取得
		Class<?>[] types = met.getParameterTypes();	
		
		//プレイヤー専用コマンドか
		boolean po = types[0].equals(Player.class);
					
		//一番目がCommandSenderかPlayerでなければ無効
		if(!po && !types[0].equals(CommandSender.class)){
			throw new InvalidCommandException("一つ目のコマンド引数型が不正です。");
		}

		//パーサー配列作成。types[0]は無視
		ArrayList<Parser<String, ?>> pars = new ArrayList<Parser<String, ?>>();
		for(int i = 1; i < types.length; i++){
			//パーサー取得
			Parser<String, ?> par = parsers.get(types[i]);
			if(par == null) throw new InvalidCommandException("サポートしていない引数型です。");
			//入れる
			pars.add(i - 1, par);
		}
		
		//コマンド登録
		registerCommand(new CommandMethod(met, obj, pars, po, prop));
	}
	
	/**
	 * コマンド実行処理をCommandManagerに移譲するためのもの
	 */
	private class CmdExecDelegate implements CommandExecutor{
		@Override
		public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3){
			return execCommand(arg0, arg1, arg3);
		}
	}
}











