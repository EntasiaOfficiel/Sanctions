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
			try{
				ProxiedPlayer p = Main.main.getProxy().getPlayer(args[0]);
				if(p==null)return "§cCe joueur n'est pas connecté ou n'existe pas !";

				User u = manager.getUser(p.getUniqueId());
				if (u == null) return "§cImpossible de charger les données de cet utilisateur !";

				if(u.hasPermission(muteExcept).asBoolean()&&!sender.hasPermission("restricted.sancmaster")){
					return "§cTu ne peut pas muter ce joueur !";
				}

				SanctionEntry se = new SanctionEntry();

				if(args[1].equalsIgnoreCase("def")||args[1].equalsIgnoreCase("inf"))se.time = -1;
				else{
					se.time = TextUtils.timeToSeconds(args[1]);
					if(se.time<=0)return "§cTemps "+args[1]+" invalide !";
				}

				se.on = p.getName();
//				se.ip = p.getPendingConnection().getAddress().getAddress().getAddress(); // pas d'ip pour le mute ?
				se.by = sender.getName();
				se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				if(se.reason.equals(""))se.reason = "Aucune";
				se.when = Calendar.getInstance();

				se.type = 1;


				se.id = Utils.requ(1,"INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
						"(?, ?, ?, ?, ?, ?, ?)", se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

				Main.sql.fastUpdate("INSERT INTO actuals (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
						"(?, ?, ?, ?, ?, ?, ?)", se.id, se.on, se.by, se.type, se.when.getTimeInMillis(), se.time, se.reason);

				Utils.mutes.add(se);

				if(silent){
					ChatComponent cc = new ChatComponent("§cSanction §ldiscrète§c : §8"+sender.getName()+"§c à muté §8"+se.on+"§c !"+Main.c);
					cc.setHoverEvent(se.getHover());
					ServerUtils.permMsg("sanctions.notify.ban", cc.create());
				}else{
					ChatComponent cc = new ChatComponent("§cSanction : §8"+sender.getName()+"§c à muté §8"+se.on+"§c ! "+Main.c);
					cc.setHoverEvent(se.getHover());
					Main.main.getProxy().broadcast(cc.create());
				}

				return "§c"+se.on +" à été muté avec succès !";

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
