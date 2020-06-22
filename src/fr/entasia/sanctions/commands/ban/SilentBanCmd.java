package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SilentBanCmd extends Command {

	public SilentBanCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.ban")){
			if(sender.hasPermission("sanctions.use.ban.silent")){
				sender.sendMessage(ChatComponent.create(BanCmd.execBan(sender, args, false)));
			}else sender.sendMessage(ChatComponent.create("§cTu n'as pas la permission de bannir silencieusement !"));

		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
