package fr.entasia.sanctions.commands.mute;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.commands.mute.MuteCmd;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentMuteCmd extends Command {

	public SilentMuteCmd() {
		super("smute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.mute")){
			if(sender.hasPermission("sanctions.mute.silent")){
				sender.sendMessage(ChatComponent.create(MuteCmd.execMute(sender, args, false)));
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de bannir silencieusement !"));

		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}