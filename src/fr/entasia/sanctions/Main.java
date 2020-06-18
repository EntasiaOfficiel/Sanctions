package fr.entasia.sanctions;

import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.sanctions.commands.infos.BanListCmd;
import fr.entasia.sanctions.commands.infos.CheckCmd;
import fr.entasia.sanctions.commands.infos.HistoryCmd;
import fr.entasia.sanctions.commands.StopCmd;
import fr.entasia.sanctions.commands.ban.BanCmd;
import fr.entasia.sanctions.commands.ban.SilentBanCmd;
import fr.entasia.sanctions.commands.ban.UnbanCmd;
import fr.entasia.sanctions.commands.mute.MuteCmd;
import fr.entasia.sanctions.commands.mute.SilentMuteCmd;
import fr.entasia.sanctions.commands.mute.UnmuteCmd;
import fr.entasia.sanctions.commands.others.KickCmd;
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
	2 - kick
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

			getProxy().getPluginManager().registerCommand(this, new StopCmd());

			getProxy().getPluginManager().registerCommand(this, new CheckCmd());
			getProxy().getPluginManager().registerCommand(this, new HistoryCmd());
			getProxy().getPluginManager().registerCommand(this, new BanListCmd());

			getProxy().getPluginManager().registerCommand(this, new BanCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentBanCmd());
			getProxy().getPluginManager().registerCommand(this, new UnbanCmd());

			getProxy().getPluginManager().registerCommand(this, new MuteCmd());
			getProxy().getPluginManager().registerCommand(this, new SilentMuteCmd());
			getProxy().getPluginManager().registerCommand(this, new UnmuteCmd());

			getProxy().getPluginManager().registerCommand(this, new KickCmd());

			ResultSet rs = sql.fastSelectUnsafe(
					"SELECT playerdata.global.address, sanctions.actuals.* FROM actuals INNER JOIN playerdata.global ON sanctions.actuals.on = playerdata.global.name"
			);
			SanctionEntry se;
			while(rs.next()){
				se = new SanctionEntry();
				se.id = rs.getInt("id");
				se.on = rs.getString("on");
				se.by = rs.getString("by");
				se.when = Calendar.getInstance();
				se.when.setTimeInMillis(rs.getLong("when"));
				se.time = rs.getInt("time");
				se.reason = rs.getString("reason");

				se.type = rs.getByte("type");

				switch(se.type){
					case 0:{
						se.ip = InetAddress.getByName(rs.getString("address")).getAddress();
						Utils.bans.add(se);
						break;
					}
					case 1:{
						Utils.mutes.add(se);
						break;
					}
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
