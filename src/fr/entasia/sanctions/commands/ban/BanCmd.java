package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class BanCmd extends Command {


	private static final Node banExcept = Main.lpAPI.buildNode("sanctions.ban.except").build();


	public BanCmd() {
		super("ban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.ban")){
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
					if(u.hasPermission(banExcept).asBoolean()){
						return "§cTu ne peut pas bannir cet utilisateur !";
					}
				}

				SanctionEntry se = new SanctionEntry();

				se.time = Utils.parseTime(args[1]);
				if(se.time<=0)return "§cTemps "+args[1]+" invalide !";

				se.on = rs.getString("name");
				se.ip = InetAddress.getByName(rs.getString("address")).getAddress();
				se.by = sender.getName();
				se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				if(se.reason.equals(""))se.reason = "Aucune";
				se.when = Calendar.getInstance();


				while(true){
					se.id = Utils.genID();
					try{
						Main.sql.fastUpdateUnsafe("INSERT INTO actuals (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES (?, ?, ?, ?, ?, ?, ?)",
								se.id, se.on, se.by, 0, se.when.getTimeInMillis(), se.time, se.reason);
						break;
					}catch(SQLException e){
						// qui à une meilleur idée pour détecter les erreurs de clés primaires ?
						if(!(e.getMessage().contains("Duplicate")&&e.getMessage().contains("PRIMARY"))){
							throw e;
						}
					}
				}

				Main.sql.fastUpdate("INSERT INTO history (`id`, `on`, `by`, `type`, `when`) VALUES " +
						"(?, ?, ?, ?, ?)", se.id, se.on, se.by, 0, se.when.getTimeInMillis());

				Main.sql.fastUpdate("INSERT INTO modifiers (`id`, `by`, `when`, `new_time`, `new_reason`) VALUES " +
						"(?, ?, ?, ?, ?)", se.id, se.by, se.when.getTimeInMillis(), se.time, se.reason);

				Utils.bans.add(se);

				return "§c"+se.on +" à été banni avec succès !";

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
