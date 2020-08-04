package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.apis.utils.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.BanEntry;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class BanCmd extends Command {

	public BanCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.ban")){
			sender.sendMessage(ChatComponent.create(execBan(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static String execBan(CommandSender sender, String[] args, boolean silent){
		if(args.length<2){
			return "§cSyntaxe : /"+(silent ? "silent" : "")+"ban <pseudo> <temps/def> [raison]";
		}
		try{
			BanEntry se = null;
			for(BanEntry lse : Utils.bans){
				if(lse.on.equals(args[0])){
					se = lse;
					break;
				}
			}
			if(se==null)return createBan(sender, args, silent);
			else return modifyBan(sender, args, silent, se);
		}catch(SQLException e){
			e.printStackTrace();
			Main.sql.broadcastError();
			return "§cUne erreur SQL s'est produite ! Contacte iTrooz_ !";
		}catch(Exception e){
			e.printStackTrace();
			return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
		}
	}




	public static String createBan(CommandSender sender, String[] args, boolean silent) throws Exception {

		ResultSet rs = Main.sql.fastSelectUnsafe("SELECT name,uuid,address FROM playerdata.global WHERE name=?", args[0]);
		BanEntry se = new BanEntry();
		if (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			User u = Main.lpAPI.getUserManager().getUser(uuid);
			if (u == null) {
				try {
					u = Main.lpAPI.getUserManager().loadUser(uuid).get();
				} catch (Exception e) {
					e.printStackTrace();
					return "§cImpossible de charger les données de cet utilisateur ! (Erreur LuckPerms, contacte iTrooz_)";
				}
				if (u == null) return "§cImpossible de charger les données de cet utilisateur !";
			}
			if (Utils.hasPermission(u, "sanctions.except.ban") && !sender.hasPermission("restricted.sancmaster")) {
				return "§cTu ne peut pas bannir ce joueur !";
			}
			se.on = rs.getString("name");
			se.ip = InetAddress.getByName(rs.getString("address")).getAddress();
		}else{
			sender.sendMessage(ChatComponent.create("§4Attention : "+args[0]+" ne s'est jamais connecté"));
			se.on = args[0];
		}

		se.by = sender.getName();
		se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if (se.reason.equals("")) se.reason = "Aucune";
		se.when = Calendar.getInstance();

		se.type = 0;

		if (args[1].equalsIgnoreCase("def") || args[1].equalsIgnoreCase("inf")) se.time = -1;
		else {
			se.time = TextUtils.timeToSeconds(args[1]);
			if (se.time <= 0) return "§cTemps " + args[1] + " invalide !";
		}


		se.id = Utils.requ(1, "INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?)", se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

		Main.sql.fastUpdate("INSERT INTO actuals (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?)", se.id, se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);


		Main.sql.fastUpdate("DELETE FROM global.reports WHERE reported = ?", se.on);

		Utils.bans.add(se);
		ProxiedPlayer p = Main.main.getProxy().getPlayer(se.on);
		if (p != null) p.disconnect(se.genBanReason().create());

		if (silent) {
			ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8" + sender.getName() + "§c à banni §8" + se.on + "§c ! " + Main.c);
			cc.setHoverEvent(se.getHover());
			ServerUtils.permMsg("sanctions.notify.ban", cc.create());
		} else {
			ChatComponent cc = new ChatComponent("§cSanction : §8" + sender.getName() + "§c à banni §8" + se.on + "§c ! " + Main.c);
			cc.setHoverEvent(se.getHover());
			Main.main.getProxy().broadcast(cc.create());
		}

		Utils.sendSancEmbed(se);

		return "§c" + se.on + " à été banni avec succès !";
	}




	public static String modifyBan(CommandSender sender, String[] args, boolean silent, BanEntry se) {
		if (!se.by.equals(sender.getName())){
			if (sender.hasPermission("sanctions.override.ban")) {
				sender.sendMessage(ChatComponent.create("§4Attention : tu modifie une sanction qui n'est pas la tienne"));
			}else{
				return "§cTu ne peux pas modifier cette sanction car elle à été faite par "+se.by+" !";
			}
		}

		int newTime;
		String newReason;
		if(args[1].equalsIgnoreCase("def")||args[1].equalsIgnoreCase("inf"))newTime = -1;
		else {
			newTime = TextUtils.timeToSeconds(args[1]);
			if (newTime <= 0) return "§cTemps " + args[1] + " invalide !";
		}

		newReason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if(newReason.equals("")){
			newReason = "Aucune";
			Utils.sendModifSancEmbed(se, sender.getName(), newTime, newReason);
		}else{
			Utils.sendModifSancEmbed(se, sender.getName(), newTime, newReason);
			se.reason = newReason;
		}
		se.time = newTime;



		Main.sql.fastUpdate("UPDATE actuals SET time=?, reason=? WHERE id=?", se.time, se.reason, se.id);
		Main.sql.fastUpdate("UPDATE history SET time=?, reason=? WHERE id=?", se.time, se.reason, se.id);

		if(silent){
			ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8"+sender.getName()+
					"§c à mis à jour le bannissement de §8"+se.on+"§c ! "+Main.c);
			cc.setHoverEvent(se.getHover());
			ServerUtils.permMsg("sanctions.notify.ban", cc.create());
		}else{
			ChatComponent cc = new ChatComponent("§cSanction : §8"+sender.getName()+
					"§c à mis à jour le bannissement de  §8"+se.on+"§c ! "+Main.c);
			cc.setHoverEvent(se.getHover());
			Main.main.getProxy().broadcast(cc.create());
		}



		return "§cTu as bien modifié la sanction de §8"+se.on+"§c !";
	}
}
