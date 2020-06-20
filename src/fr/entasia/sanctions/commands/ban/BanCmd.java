package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.ServerUtils;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.listeners.Base;
import fr.entasia.sanctions.utils.BanEntry;
import fr.entasia.sanctions.utils.MuteEntry;
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

public class BanCmd extends Command {


	private static final Node banExcept = Main.lpAPI.buildNode("sanctions.except.ban").build();


	public BanCmd() {
		super("ban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.ban")){
			sender.sendMessage(ChatComponent.create(execBan(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static String execBan(CommandSender sender, String[] args, boolean silent){
		if(args.length<2){
			String cmd;
			if(silent)cmd = "silentban";
			else cmd = "ban";
			return "§cSyntaxe : /"+cmd+" <pseudo> <temps/def> [raison]";
		}else{
			try{
				BanEntry se = null;
				for(BanEntry lse : Utils.bans){
					if(lse.on.equals(args[0])){
						se = lse;
						break;
					}
				}
				if(se==null){
					ResultSet rs = Main.sql.fastSelectUnsafe("SELECT name,uuid,address FROM playerdata.global WHERE name=?", args[0]);
					if(rs.next()) {
						UUID uuid = UUID.fromString(rs.getString("uuid"));
						User u = manager.getUser(uuid);
						if (u == null) {
							try {
								u = manager.loadUser(uuid).get();
							} catch (Exception e) {
								e.printStackTrace();
								return "§cImpossible de charger les données de cet utilisateur ! (Erreur LuckPerms, contacte iTrooz_)";
							}
							if (u == null) return "§cImpossible de charger les données de cet utilisateur !";
						}
						if(u.hasPermission(banExcept).asBoolean()&&!sender.hasPermission("restricted.sancmaster")){
							return "§cTu ne peut pas bannir ce joueur !";
						}
					}

					se = new BanEntry();

					if(args[1].equalsIgnoreCase("def")||args[1].equalsIgnoreCase("inf"))se.time = -1;
					else{
						se.time = TextUtils.timeToSeconds(args[1]);
						if(se.time<=0)return "§cTemps "+args[1]+" invalide !";
					}

					se.on = rs.getString("name");
					se.ip = InetAddress.getByName(rs.getString("address")).getAddress();
					se.by = sender.getName();
					se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					if(se.reason.equals(""))se.reason = "Aucune";
					se.when = Calendar.getInstance();

					se.type = 0;


					se.id = Utils.requ(1,"INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
							"(?, ?, ?, ?, ?, ?, ?)", se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

					Main.sql.fastUpdate("INSERT INTO actuals (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
							"(?, ?, ?, ?, ?, ?, ?)", se.id, se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

					Utils.bans.add(se);
					ProxiedPlayer p = Main.main.getProxy().getPlayer(rs.getString("name"));
					if(p!=null)p.disconnect(se.genBanReason().create());

					if(silent){
						ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8"+sender.getName()+"§c à banni §8"+se.on+"§c ! "+Main.c);
						cc.setHoverEvent(se.getHover());
						ServerUtils.permMsg("sanctions.notify.ban", cc.create());
					}else{
						ChatComponent cc = new ChatComponent("§cSanction : §8"+sender.getName()+"§c à banni §8"+se.on+"§c ! "+Main.c);
						cc.setHoverEvent(se.getHover());
						Main.main.getProxy().broadcast(cc.create());
					}
					return "§c"+se.on +" à été banni avec succès !";
				}else{
					if (!se.by.equals(sender.getName())){
						if (sender.hasPermission("sanctions.override.ban")) {
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

					String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					if(!reason.equals(""))se.reason = reason;

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
			}catch(SQLException e){
				e.printStackTrace();
				Main.sql.broadcastError();
				return "§cUne erreur SQL s'est produite ! Contacte iTrooz_ !";
			}catch(Exception e){
				e.printStackTrace();
				return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
			}
		}
	}

	public static UserManager manager = Main.lpAPI.getUserManager();
}
