package com.github.ebitiri.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 * CommandManagerによって管理されるコマンドの実体となるメソッドに付加するデータ
 * [メソッドの定義方法]
 * 1)publicで非static、戻り値はvoid メソッドの名前は任意。
 * 2)このアノテーションを付加する。
 * 3)一番目の引数型はCommandSenderないしPlayerにする。後者の場合はコンソールから実行できないコマンドになる。
 * 4)二番目以降の引数型は、定義したいコマンドの引数に相当するものにする。
 * ただし、この引数型は管理するCommandManagerの
 * {@link CommandManager#registerParser(com.github.ebitiri.parser.Parser, Class)}
 * で登録された型である必要がある。
 * このパーサーを用いてコマンド引数がStringから変換された上で、メソッドに届けられる。
 * [例]
 * {@literal @}CommandProperty(
 * 	namespace = "foo"
 * 	name = "bar"
 * 	perm = "hoge"
 * )
 * public void cmd(CommandSender sender, int a, String b){
 * 	sender.sendMessage(b + a * a);
 * }
 * とすると /foo barコマンドが登録される。
 *  /foo bar 10 ・。・  とすると、送信者に ・。・100 というメッセージが返される。
 * このコマンドを実行するにはhogeというパーミッションが必要。
 * </pre>
 * @author ebi
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandProperty{

	/**
	 * コマンドの名前<br>
	 *  /foo bar の bar<br>
	 *  /foo というコマンドにしたい場合は""を指定。このコマンドは名前空間fooの無名コマンドとして扱う。
	 */
	String name();
	
	/**
	 * コマンドの名前空間<br>
	 *　 /foo bar のfoo
	 */
	String namespace();
	
	/**
	 * このシステムで独自にチェックするパーミッション<br>
	 *  /foo bar コマンドは、bukkit管理下では /foo + 引数　とみなされており、 /foo のパーミッションが適用される。<br>
	 *  /foo bar　に対してパーミッションを設定したい場合はこれを用いる。<br>
	 *  ただし、/fooのパーミッションチェックは平常通りなされ、/foo bar　のチェックはそれに追加して行う形になる。<br>
	 *  不要な場合は""を指定
	 * @return
	 */
	String perm();
}
