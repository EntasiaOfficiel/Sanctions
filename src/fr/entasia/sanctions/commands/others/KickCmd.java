package fr.entasia.sanctions.commands.others;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.ServerUtils;
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
		if (sender.hasPermission("sanctions.use.kick")) {
			sender.sendMessage(ChatComponent.create(execKick(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static String execKick(CommandSender sender, String[] args, boolean silent){
		if(args.length==0){
			return "§cSyntaxe : /"+(silent ? "silent" : "")+"kick <pseudo> <temps/def> [raison]";
		}
		ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
		if(target==null)return "§8"+args[0]+" §cn'est pas connecté ou n'existe pas !";
		else if(target.hasPermission("sanctions.except.kick")&&!sender.hasPermission("restricted.sancmaster")){
			return "§cTu ne peux pas kick §8"+target.getName()+"§c !";
		}else {
			MuteEntry se = new MuteEntry(); // pas une vraie, juste structure
			se.type = 2;
			se.on = target.getName();
			se.by = sender.getName();
			se.when = Calendar.getInstance();

			if (args.length == 1) se.reason = "Aucune";
			else se.reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			ChatComponent cc = new ChatComponent("§c§m----§c  Tu as été kick !  §c§m----",
					"",
					"§cPar : §8" + sender.getName(),
					"§cRaison : §8" + se.reason
			);
			target.disconnect(cc.create());

			if (silent) {
				cc = new ChatComponent("§cSanction §ldiscrète§c : §8" + sender.getName() + "§c à kick §8" + se.on + "§c !" + Main.c);
				cc.setHoverEvent(se.getHover());
				ServerUtils.permMsg("sanctions.notify.kick", cc.create());
			} else {
				cc = new ChatComponent("§cSanction : §8" + sender.getName() + "§c à kick §8" + se.on + "§c ! " + Main.c);
				cc.setHoverEvent(se.getHover());
				Main.main.getProxy().broadcast(cc.create());
			}

			try {
				Utils.requ(1, "INSERT INTO history (`id`, `on`, `by`, `type`, `when`, `time`, `reason`) VALUES " +
						"(?, ?, ?, ?, ?, ?, ?)", target.getName(), sender.getName(), 2, new Date().getTime(), 0, se.reason);


				Utils.sendSancEmbed(se);

				return "§cTu as kick §8" + target.getName() + "§c !";
			} catch (SQLException e) {
				e.printStackTrace();
				Main.sql.broadcastError();
				return "§cUne erreur SQL s'est produite ! Contacte iTrooz_ !";
			}
		}
	}
}
