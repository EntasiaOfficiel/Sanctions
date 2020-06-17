package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class UnbanCmd extends Command {

	public UnbanCmd() {
		super("ipban", null, "banip");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.unban")){
			if(args.length==1){
				SanctionEntry se;
				if(args[0].contains(".")){
					try {
						byte[] ip = InetAddress.getByName(args[0]).getAddress();
						for(SanctionEntry l : Utils.bans){
							if(Arrays.equals(ip, l.ip)){
								se = l;
								break;
							}
						}
					} catch (UnknownHostException e) {
						sender.sendMessage(ChatComponent.create("§cL'adresse IP "+args[0]+" est invalide !"));
						return;
					}
				}
			}else sender.sendMessage(ChatComponent.create("§cSyntaxe : /unban <player>"));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
