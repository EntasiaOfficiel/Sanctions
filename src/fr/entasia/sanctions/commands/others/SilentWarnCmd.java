package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentWarnCmd extends Command {

	public SilentWarnCmd() {
		super("swarn");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.warn")){
			if(sender.hasPermission("sanctions.warn.silent")){
				sender.sendMessage(ChatComponent.create(WarnCmd.execWarn(sender, args, false)));
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de bannir silencieusement !"));

		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
