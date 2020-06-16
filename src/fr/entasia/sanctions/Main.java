package fr.entasia.sanctions;

import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.sanctions.commands.*;
import fr.entasia.sanctions.commands.ban.BanCmd;
import fr.entasia.sanctions.commands.ban.IPBanCmd;
import fr.entasia.sanctions.commands.ban.SilentBanCmd;
import fr.entasia.sanctions.commands.ban.SilentIPBanCmd;
import fr.entasia.sanctions.commands.others.MuteCmd;
import fr.entasia.sanctions.commands.others.SilentMuteCmd;
import fr.entasia.sanctions.commands.others.SilentWarnCmd;
import fr.entasia.sanctions.commands.others.WarnCmd;
import fr.entasia.sanctions.listeners.Base;
import fr.entasia.sanctions.utils.SanctionEntry;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Random;

public class Main extends Plugin {


	/*
	0 - ban
	1 - mute
	2 - warn
	 */

	public static final char c = '♧';

	public static Main main;
	public static SQLConnection sql;
	public static LuckPermsApi lpAPI;

	public static Random random = new Random();

	@Override
	public void onEnable(){
		try{
			getLogger().info("Activation du plugin..");
			main = this;
			sql = new SQLConnection("root", "sanctions"); // sanctions
			lpAPI = LuckPerms.getApi();

			getProxy().getPluginManager().registerListener(this, new Base());

			getProxy().getPluginManager().registerCommand(this, new TestCmd());

			getProxy().getPluginManager().registerCommand(this, new CheckCmd());
			getProxy().getPluginManager().registerCommand(this, new HistoryCmd());
			getProxy().getPluginManager().registerCommand(this, new BanCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentBanCmd());
			getProxy().getPluginManager().registerCommand(this, new IPBanCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentIPBanCmd());
			getProxy().getPluginManager().registerCommand(this, new MuteCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentMuteCmd());
			getProxy().getPluginManager().registerCommand(this, new WarnCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentWarnCmd());

			ResultSet rs = sql.fastSelectUnsafe("SELECT global.address, * FROM actuals INNER JOIN global ON actuals.on = actuals.name");
			SanctionEntry se;
			while(rs.next()){
				se = new SanctionEntry();
				se.id = rs.getInt("id");
				se.on = rs.getString("banned");
				se.by = rs.getString("by");
				se.when = Calendar.getInstance();
				se.when.setTimeInMillis(rs.getLong("when"));
				se.time = rs.getInt("time");
				se.reason = rs.getString("reason");

				switch(rs.getByte("type")){
					case 0:{
						se.ip = InetAddress.getByName(rs.getString("address")).getAddress();
						Utils.bans.add(se);
						break;
					}
					case 1:{
						Utils.mutes.add(se);
						break;
					}
//					case 2:{
//						SanctionTypes.WA.add(se);
//						break;
//					}
					default:{
						getLogger().warning("ID de type invalide : "+rs.getByte("type"));
						break;
					}
				}
			}


			getLogger().info("Plugin activé !");
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("ARRET DU SERVEUR !");
			getProxy().stop();
		}
	}
}