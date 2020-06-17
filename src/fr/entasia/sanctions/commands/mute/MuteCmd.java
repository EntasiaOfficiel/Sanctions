package fr.entasia.sanctions.commands.mute;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import me.lucko.luckperms.api.Node;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MuteCmd extends Command {

	private static final Node muteExcept = Main.lpAPI.buildNode("sanctions.except.ban").build();

	public MuteCmd() {
		super("mute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.mute")){
			sender.sendMessage(ChatComponent.create(execMute(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));


	}

	public static String execMute(CommandSender sender, String[] args, boolean silent){
		return "§cUne erreur interne s'est produite ! Contacte iTrooz_ !";
	}

}