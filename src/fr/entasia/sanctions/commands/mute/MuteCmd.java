package fr.entasia.sanctions.commands.mute;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.ServerUtils;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.listeners.Base;
import fr.entasia.sanctions.utils.SanctionEntry;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class MuteCmd extends Command {

	private static final Node muteExcept = Main.lpAPI.buildNode("sanctions.except.mute").build();

	public MuteCmd() {
		super("mute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.mute")){
			sender.sendMessage(ChatComponent.create(execMute(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));


	}

	public static String execMute(CommandSender sender, String[] args, boolean silent){
		if(args.length<2){
			String cmd;
			if(silent)cmd = "silentmute";
			else cmd = "mute";
			return "§cSyntaxe : /"+cmd+" <pseudo> <temps/def> [raison]";
		}else{


			ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
			if (target == null) return "§cCe joueur n'est pas connecté ou n'existe pas !";

			try{
				SanctionEntry se = null;
				for(SanctionEntry lse : Utils.bans){
					if(lse.on.equals(args[0])){
						se = lse;
						break;
					}
				}
				if(se==null) {

					User u = manager.getUser(target.getUniqueId());
					if (u == null) return "§cImpossible de charger les données de cet utilisateur !";

					if (u.hasPermission(muteExcept).asBoolean() && !sender.hasPermission("restricted.sancmaster")) {
						return "§cTu ne peut pas muter ce joueur !";
					}

					se = new SanctionEntry();

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
						ServerUtils.permMsg("sanctions.notify.ban", cc.create());
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
					return "§c" + se.on + " à été muté avec succès !";
				}else{
					if (!se.by.equals(sender.getName())){
						if (sender.hasPermission("sanctions.override.mute")) {
							sender.sendMessage(ChatComponent.create("§4Attention : tu modifie une sanction qui n'est pas la tienne"));
						}else{
							return "§cTu ne peux pas modifier cette sanction car elle à été faite par "+se.by+" !";
						}
					}


					if(args[1].equalsIgnoreCase("def")||args[1].equalsIgnoreCase("inf"))se.time = -1;
					else {
						se.time = TextUtils.timeToSeconds(args[1]);
						if (se.time <= 0) return "§cTemps " + args[1] + " invalide !";
					}

					ChatComponent msg = new ChatComponent(
							"§cTon mute à été modifié :",
					"§cNouveau temps : §8"+TextUtils.secondsToTime(se.time));

					String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					if(!reason.equals("")){
						se.reason = reason;
						msg.append("\n§cNouvelle raison : §8"+reason);
					}

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

			}catch(SQLException e){
				e.printStackTrace();
				Main.sql.broadcastError();
				return "§cUne erreur SQL s'est produite ! Contacte iTrooz_ !";
			}catch(Exception e){
				return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
			}
		}
	}

	public static UserManager manager = Main.lpAPI.getUserManager();

}
