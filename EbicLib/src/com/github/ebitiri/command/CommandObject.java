package com.github.ebitiri.command;

import org.bukkit.command.CommandSender;



/**
 * CommandManagerが管理するコマンドを表すインターフェース
 * @author ebi
 */
public interface CommandObject{

	public void exec(CommandSender sender, String[] args) throws CommandExecException;
	
	public String getPerm();
	public String getName();
	public String getNameSpace();
	public boolean isPlayerCommand();
}
