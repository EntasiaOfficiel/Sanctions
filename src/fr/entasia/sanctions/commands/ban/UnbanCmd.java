package fr.entasia.sanctions.commands.ban;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.ServerUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

public class UnbanCmd extends Command {

	public UnbanCmd() {
		super("unban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.unban")){
			if(args.length>0){
				SanctionEntry se = null;
				if(args[0].contains(".")){
					try {
						byte[] ip = InetAddress.getByName(args[0]).getAddress();
						for(SanctionEntry l : Utils.bans){
							if(Arrays.equals(ip, l.ip)){
								se = l;
								break;
							}
						}
						if(se==null){
							sender.sendMessage(ChatComponent.create("§cCette adresse IP n'est pas bannie !"));
						}
					} catch (UnknownHostException e) {
						sender.sendMessage(ChatComponent.create("§cL'adresse IP "+args[0]+" est invalide !"));
					}
				}else {
					for (SanctionEntry l : Utils.bans) {
						if (args[0].equals(l.on)) {
							se = l;
							break;
						}
					}
					if (se == null) {
						sender.sendMessage(ChatComponent.create("§cCe pseudo n'est pas banni !"));
					}
				}
				if(se==null)return;

				if (!se.by.equals(sender.getName())){
					if (sender.hasPermission("sanctions.override.ban")) {
						sender.sendMessage(ChatComponent.create("§4Attention : tu modifie une sanction qui n'est pas la tienne"));
					}else{
						sender.sendMessage(ChatComponent.create("§cTu ne peux pas modifier cette sanction car elle à été faite par "+se.by+" !"));
						return;
					}
				}

				String reason;
				if(args.length==1)reason = "Aucune";
				else reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

				Utils.bans.remove(se);
				if(Main.sql.fastUpdate("DELETE FROM actuals WHERE `on`=?", se.on)==-1||
						Main.sql.fastUpdate("UPDATE history SET unban_by=?, unban_when=?, unban_reason=? WHERE id=?",
								sender.getName(), new Date().getTime(), reason, se.id)==-1){
					sender.sendMessage(ChatComponent.create("§cUne erreur SQL s'est produite !"));
				}else {

					ChatComponent cc = new ChatComponent("§c§lUnban§c : §8"+sender.getName()+"§c à débanni §8"+se.on+"§c !"+Main.c);
					cc.setHoverEvent(se.getHover());
					ServerUtils.permMsg("sanctions.notify.unban", cc.create());

					sender.sendMessage(ChatComponent.create("§c" + se.on + " à été débanni avec succès !"));
				}
			}else sender.sendMessage(ChatComponent.create("§cSyntaxe : /unban <player>"));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
