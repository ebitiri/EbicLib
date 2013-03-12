EbicLib
=======

bukkitプラグイン用のライブラリ的ななにか。

・簡単にコマンドが定義できる

@CommandProperty(name = "hoge", namespace = "foo", perm = "")
  public void hoge(CommandSender sender, int a, String b){
		sender.sendMessage(b + a * a);
	}

/foo hoge 10 test  と打つと test100 というメッセージが返る
