package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.MuteEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class KickCmd extends Command {

	public KickCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.kick")){
			if(args.length==0)sender.sendMessage(ChatComponent.create("§cSyntaxe : /kick <pseudo/IP> [raison]"));
			else{
				ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
				if(target==null)sender.sendMessage(ChatComponent.create("§8"+args[0]+" §cn'est pas connecté ou n'existe pas !"));
				else if(target.hasPermission("sanctions.except.kick")&&!sender.hasPermission("restricted.sancmaster")){
					sender.sendMessage(ChatComponent.create("§cTu ne peux pas kick §8"+target.getName()+"§c !"));
				}else{

					MuteEntry se = new MuteEntry(); // pas une vraie, juste structure
					se.type = 2;
					se.on = target.getName();
					se.by = sender.getName();
					se.when = Calendar.getInstance();

					if(args.length==1)se.reason = "Aucune";
					else se.reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					ChatComponent cc = new ChatComponent("§c§m----§c  Tu à été kick !  §c§m----",
							"",
							"§cPar : §8"+sender.getName(),
							"§cRaison : §8"+se.reason
							);
					target.disconnect(cc.create());

					try{
						Utils.requ(1,"INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
							"(?, ?, ?, ?, ?, ?, ?)", target.getName(), sender.getName(), 2, new Date().getTime(), 0, se.reason);

						Utils.sendSancEmbed(se);

						sender.sendMessage(ChatComponent.create("§cTu as kick §8"+target.getName()+"§c !"));
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
