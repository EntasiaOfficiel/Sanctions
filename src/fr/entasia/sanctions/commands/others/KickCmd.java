package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

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
					String reason;
					if(args.length==1)reason = "Aucune";
					else reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					ChatComponent cc = new ChatComponent("§c§m----§c  Tu à été kick !  §c§m----",
							"",
							"§cPar : §7"+sender.getName(),
							"§cRaison : §7"+reason
							);
					target.disconnect(cc.create());

					try{
						Utils.requ(1,"INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
							"(?, ?, ?, ?, ?, ?, ?)", target.getName(), sender.getName(), 2, new Date().getTime(), 0, reason);
						sender.sendMessage(ChatComponent.create("§cTu as kick §7"+target.getName()+"§c !"));
					}catch(SQLException e){
						e.printStackTrace();
						Main.sql.broadcastError();
						sender.sendMessage(ChatComponent.create("§cUne erreur SQL s'est produite ! Contacte iTrooz_ !"));
					}
			}
		}
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

}
