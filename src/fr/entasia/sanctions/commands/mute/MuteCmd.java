package fr.entasia.sanctions.commands.mute;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.apis.utils.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.MuteEntry;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;

public class MuteCmd extends Command {

	public MuteCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		System.out.println("mute exec");
		if(sender.hasPermission("sanctions.use.mute")){
			sender.sendMessage(ChatComponent.create(execMute(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static String execMute(CommandSender sender, String[] args, boolean silent){
		if(args.length<2){
			return "§cSyntaxe : /"+(silent ? "silent" : "")+"mute <pseudo> <temps/def> [raison]";
		}
		ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
		if (target == null) return "§cCe joueur n'est pas connecté ou n'existe pas !";

		try{
			MuteEntry se = null;
			for(MuteEntry lse : Utils.mutes){
				if(lse.on.equals(args[0])){
					se = lse;
					break;
				}
			}
			if(se==null) return createMute(sender, target, args, silent);
			else return modifyMute(sender, target, args, silent, se);

		}catch(SQLException e){
			e.printStackTrace();
			Main.sql.broadcastError();
			return "§cUne erreur SQL s'est produite ! Contacte iTrooz_ !";
		}catch(Exception e){
			return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
		}
	}


	public static String createMute(CommandSender sender, ProxiedPlayer target, String[] args, boolean silent) throws Exception {

		User u = Main.lpAPI.getUserManager().getUser(target.getUniqueId());
		if (u == null) return "§cImpossible de charger les données de cet utilisateur !";

		if (Utils.hasPermission(u, "sanctions.except.mute") && !sender.hasPermission("restricted.sancmaster")) {
			return "§cTu ne peut pas muter ce joueur !";
		}

		MuteEntry se = new MuteEntry();

		if (args[1].equalsIgnoreCase("def") || args[1].equalsIgnoreCase("inf")) se.time = -1;
		else {
			se.time = TextUtils.timeToSeconds(args[1]);
			if (se.time <= 0) return "§cTemps " + args[1] + " invalide !";
		}

		se.on = target.getName();
		//				se.ip = p.getPendingConnection().getAddress().getAddress().getAddress(); // pas d'ip pour le mute ?
		se.by = sender.getName();
		se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if (se.reason.equals("")) se.reason = "Aucune";
		se.when = Calendar.getInstance();

		se.type = 1;


		se.id = Utils.requ(1, "INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?)", se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

		Main.sql.fastUpdate("INSERT INTO actuals (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?)", se.id, se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

		Utils.mutes.add(se);

		if (silent) {
			ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8" + sender.getName() + "§c à muté §8" + se.on + "§c !" + Main.c);
			cc.setHoverEvent(se.getHover());
			ServerUtils.permMsg("sanctions.notify.mute", cc.create());
		} else {
			ChatComponent cc = new ChatComponent("§cSanction : §8" + sender.getName() + "§c à muté §8" + se.on + "§c ! " + Main.c);
			cc.setHoverEvent(se.getHover());
			Main.main.getProxy().broadcast(cc.create());
		}
		ChatComponent msg = new ChatComponent(
				"§cTu as été muté par §8"+se.by+"§c pour :",
				"§cRaison : §8"+(se.reason==null ? "Indéfini" : se.reason),
				"§cDurée : §8"+(se.time==-1 ? "Indéfinie" : TextUtils.secondsToTime(se.time))
		);
		target.sendMessage(msg.create());

		Utils.sendSancEmbed(se);

		return "§c" + se.on + " à été muté avec succès !";
	}


	public static String modifyMute(CommandSender sender, ProxiedPlayer target, String[] args, boolean silent, MuteEntry se) {

		if (!se.by.equals(sender.getName())){
			if (sender.hasPermission("sanctions.override.mute")) {
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

		ChatComponent msg = new ChatComponent(
				"§cTon mute à été modifié :",
				"§cNouveau temps : §8"+TextUtils.secondsToTime(se.time));

		newReason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if(newReason.equals("")){
			newReason = "Aucune";
			Utils.sendModifSancEmbed(se, sender.getName(), newTime, newReason);
		}else{
			Utils.sendModifSancEmbed(se, sender.getName(), newTime, newReason);
			msg.append("\n§cNouvelle raison : §8"+newReason);
			se.reason = newReason;
		}
		se.time = newTime;

		Main.sql.fastUpdate("UPDATE actuals SET time=?, reason=? WHERE id=?", se.time, se.reason, se.id);
		Main.sql.fastUpdate("UPDATE history SET time=?, reason=? WHERE id=?", se.time, se.reason, se.id);

		if(silent){
			ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8"+sender.getName()+
					"§c à mis à jour le mute de §8"+se.on+"§c ! "+Main.c);
			cc.setHoverEvent(se.getHover());
			ServerUtils.permMsg("sanctions.notify.mute", cc.create());
		}else {
			ChatComponent cc = new ChatComponent("§cSanction : §8" + sender.getName() +
					"§c à mis à jour le mute de  §8" + se.on + "§c ! " + Main.c);
			cc.setHoverEvent(se.getHover());
			Main.main.getProxy().broadcast(cc.create());
		}
		target.sendMessage(msg.create());


		return "§cTu as bien modifié la sanction de §8"+se.on+"§c !";
	}
}
