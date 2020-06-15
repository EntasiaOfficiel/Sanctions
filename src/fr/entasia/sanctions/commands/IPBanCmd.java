package fr.entasia.sanctions.commands;

import fr.entasia.apis.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class IPBanCmd extends Command {

	public IPBanCmd() {
		super("ipban", null, "banip");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.ban")){
			sender.sendMessage(ChatComponent.create(execBan(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));


	}

	public static String execBan(CommandSender sender, String[] args, boolean silent){
		return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
	}
}
