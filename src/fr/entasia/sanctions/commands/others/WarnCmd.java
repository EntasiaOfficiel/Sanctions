package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class WarnCmd extends Command {

	public WarnCmd() {
		super("warn");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.warn")){
			sender.sendMessage(ChatComponent.create(execWarn(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));


	}

	public static String execWarn(CommandSender sender, String[] args, boolean silent){
		return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
	}
}
