package fr.entasia.sanctions.commands.mute;

import fr.entasia.apis.other.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentMuteCmd extends Command {

	public SilentMuteCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.mute")){
			if(sender.hasPermission("sanctions.use.mute.silent")){
				sender.sendMessage(ChatComponent.create(MuteCmd.execMute(sender, args, true)));
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de bannir silencieusement !"));

		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
