package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.other.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentKickCmd extends Command {

	public SilentKickCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("sanctions.use.kick")) {
			if(sender.hasPermission("sanctions.use.mute.silent")){
				KickCmd.execKick(sender, args, true);
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de kick silencieusement !"));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

}
