package fr.entasia.sanctions.commands;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.PlayerUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.SanctionEntry;
import fr.entasia.sanctions.Utils;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import me.lucko.luckperms.common.node.factory.NodeBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
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
				ResultSet rs = Main.sql.fastSelectUnsafe("SELECT name,uuid FROM playerdata.global WHERE name=?", args[0]);
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
						else{
							if(u.hasPermission(banExcept).asBoolean()){
								return "§cTu ne peut pas bannir cet utilisateur !";
							}
						}
					}
				}

				SanctionEntry se = new SanctionEntry();

				se.time = Utils.parseTime(args[1]);
				if(se.time<=0)return "§cTemps "+args[1]+" invalide !";

				se.banned = rs.getString("name");
				se.by = sender.getName();
				se.reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				if(se.reason.equals(""))se.reason = "Aucune";
				se.when = new Date().getTime();

				Main.sql.fastUpdate("INSERT INTO actuals (banned, by, type, when, time, reason) VALUES" +
						"(?, ?, ?, ?, ?, ?)", se.banned, se.by, 1, se.when, se.time, se.reason);

				Utils.bans.add(se);

				sender.sendMessage(ChatComponent.create("Banni avec succès !"));

			}catch(SQLException e){
				e.printStackTrace();
				Main.sql.broadcastError();
				return "§cUne erreur SQL s'est produite !";
			}

		}


		return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
	}

	public static UserManager manager = Main.lpAPI.getUserManager();
}
