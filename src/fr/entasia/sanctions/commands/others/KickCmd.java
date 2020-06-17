package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class KickCmd extends Command {

	public KickCmd() {
		super("kick");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.kick")){
			if(args.length==0)sender.sendMessage(ChatComponent.create("§cSyntaxe : /kick <pseudo/IP> [raison]"));
			else{
				ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
				if(target==null)sender.sendMessage(ChatComponent.create("§7"+args[0]+" §cn'est pas connecté ou n'existe pas !"));
				else if(target.hasPermission("sanctions.except.kick")&&!sender.hasPermission("restricted.sancmaster")){
					sender.sendMessage(ChatComponent.create("§cTu ne peux pas kick §8"+target.getName()+"§c !"));
				}else{
					String s;
					if(args.length==1){
						s = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					}else s = "Aucune raison";
					target.disconnect();
					sender.sendMessage(ChatComponent.create("§cTu as kick §7"+target.getName()+"§c !"));
			}
		}
			sender.sendMessage(ChatComponent.create(execMute(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

}
