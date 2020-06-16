package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.commands.ban.BanCmd;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentIPBanCmd extends Command {

	public SilentIPBanCmd() {
		super("sbanip", null, "sipban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.ban")){
			if(sender.hasPermission("sanctions.ban.silent")){
				sender.sendMessage(ChatComponent.create(BanCmd.execBan(sender, args, false)));
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de bannir silencieusement !"));

		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
