package fr.entasia.sanctions;

import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.sanctions.commands.*;
import fr.entasia.sanctions.listeners.Base;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {


	/*
	0 - ipban
	1 - ban
	2 - mute
	3 - warn

	 */

	public static Main main;
	public static SQLConnection sql;
	public static LuckPermsApi lpAPI;

	@Override
	public void onEnable(){
		try{
			getLogger().info("Activation du plugin..");
			main = this;
			sql = new SQLConnection("root"); // sanctions
			lpAPI = LuckPerms.getApi();

			getProxy().getPluginManager().registerListener(this, new Base());

			getProxy().getPluginManager().registerCommand(this, new TestCmd());

			getProxy().getPluginManager().registerCommand(this, new BanCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentBanCmd());
			getProxy().getPluginManager().registerCommand(this, new IPBanCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentIPBanCmd());
			getProxy().getPluginManager().registerCommand(this, new MuteCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentMuteCmd());
			getProxy().getPluginManager().registerCommand(this, new WarnCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentWarnCmd());

			getLogger().info("Plugin activé !");
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("ARRET DU SERVEUR !");
			getProxy().stop();
		}
	}

}
