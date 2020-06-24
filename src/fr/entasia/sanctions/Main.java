package fr.entasia.sanctions;

import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.sanctions.commands.ban.BanCmd;
import fr.entasia.sanctions.commands.ban.SilentBanCmd;
import fr.entasia.sanctions.commands.ban.UnbanCmd;
import fr.entasia.sanctions.commands.infos.CheckCmd;
import fr.entasia.sanctions.commands.infos.HistoryCmd;
import fr.entasia.sanctions.commands.infos.MuteListCmd;
import fr.entasia.sanctions.commands.mute.MuteCmd;
import fr.entasia.sanctions.commands.mute.SilentMuteCmd;
import fr.entasia.sanctions.commands.mute.UnmuteCmd;
import fr.entasia.sanctions.commands.others.KickCmd;
import fr.entasia.sanctions.commands.others.SilentKickCmd;
import fr.entasia.sanctions.listeners.Base;
import fr.entasia.sanctions.utils.BanEntry;
import fr.entasia.sanctions.utils.MuteEntry;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Main extends Plugin {


	/*
	0 - ban
	1 - mute
	2 - kick
	 */

	public static final char c = '♧';

	public static Main main;
	public static SQLConnection sql;
	public static LuckPermsApi lpAPI;

	public static Random random = new Random();

	public static final ArrayList<String> cmdcompletes = new ArrayList<>();

	@Override
	public void onEnable(){
		try{
			getLogger().info("Activation du plugin..");
			main = this;
			sql = new SQLConnection("sanctions", "sanctions");
			lpAPI = LuckPerms.getApi();

			getProxy().getPluginManager().registerListener(this, new Base());


			getProxy().getPluginManager().registerCommand(this, new CheckCmd("check"));
//			getProxy().getPluginManager().registerCommand(this, new MuteListCmd("mutelist"));
			getProxy().getPluginManager().registerCommand(this, new HistoryCmd("history"));

			getProxy().getPluginManager().registerCommand(this, new BanCmd("ban"));
			getProxy().getPluginManager().registerCommand(this, new SilentBanCmd("sban", "silentban"));
			getProxy().getPluginManager().registerCommand(this, new UnbanCmd("unban"));

			getProxy().getPluginManager().registerCommand(this, new MuteCmd("mute"));
			getProxy().getPluginManager().registerCommand(this, new SilentMuteCmd("smute", "silentmute"));
			getProxy().getPluginManager().registerCommand(this, new UnmuteCmd("unmute"));

			getProxy().getPluginManager().registerCommand(this, new KickCmd("kick"));
			getProxy().getPluginManager().registerCommand(this, new SilentKickCmd("skick", "silentkick"));

			cmdcompletes.add("check");
			cmdcompletes.add("mutelist");
			cmdcompletes.add("history");

			cmdcompletes.add("ban");
			cmdcompletes.add("silentban");
			cmdcompletes.add("unban");

			cmdcompletes.add("mute");
			cmdcompletes.add("silentmute");
			cmdcompletes.add("unmute");

			cmdcompletes.add("kick");

			ResultSet rs = sql.fastSelectUnsafe(
					"SELECT playerdata.global.address, sanctions.actuals.* FROM actuals INNER JOIN playerdata.global ON sanctions.actuals.on = playerdata.global.name"
			);
			MuteEntry se;
			BanEntry ban;
			while(rs.next()){

				byte type = rs.getByte("type");

				switch(type){
					case 0:{
						ban = new BanEntry();
						ban.ip = InetAddress.getByName(rs.getString("address")).getAddress();
						Utils.bans.add(ban);
						se = ban;
						break;
					}
					case 1:{
						se = new MuteEntry();
						Utils.mutes.add(se);
						break;
					}
					default:{
						getLogger().warning("ID de type invalide : "+rs.getByte("type"));
						continue;
					}
				}
				se.id = rs.getInt("id");
				se.on = rs.getString("on");
				se.by = rs.getString("by");
				se.when = Calendar.getInstance();
				se.when.setTimeInMillis(rs.getLong("when"));
				se.time = rs.getInt("time");
				se.reason = rs.getString("reason");
			}


			getLogger().info("Plugin activé !");
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("ARRET DU SERVEUR !");
			getProxy().stop();
		}
	}
}
