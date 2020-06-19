package fr.entasia.sanctions.commands.mute;

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

public class UnmuteCmd extends Command {

	public UnmuteCmd() {
		super("unmute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.unmute")){
			if(args.length>0){
				SanctionEntry se = null;
				if(args[0].contains(".")){
					try {
						byte[] ip = InetAddress.getByName(args[0]).getAddress();
						for(SanctionEntry l : Utils.mutes){
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
					for (SanctionEntry l : Utils.mutes) {
						if (args[0].equals(l.on)) {
							se = l;
							break;
						}
					}
					if (se == null) {
						sender.sendMessage(ChatComponent.create("§cCe joueur n'est pas muté !"));
					}
				}
				if(se==null)return;

				if (!se.by.equals(sender.getName())){
					if (sender.hasPermission("sanctions.override.unmute")) {
						sender.sendMessage(ChatComponent.create("§4Attention : tu modifie une sanction qui n'est pas la tienne"));
					}else{
						sender.sendMessage(ChatComponent.create("§cTu ne peux pas modifier cette sanction car elle à été faite par "+se.by+" !"));
						return;
					}
				}

				String reason;
				if(args.length==1)reason = "Aucune";
				else reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

				Utils.mutes.remove(se);
				se.SQLDelete();
				Main.sql.fastUpdate("UPDATE history SET unban_by=?, unban_when=?, unban_reason=? WHERE id=?",
								sender.getName(), new Date().getTime(), reason, se.id);
				ChatComponent cc = new ChatComponent("§c§lUnmute§c : §8"+sender.getName()+"§c à démuté §8"+se.on+"§c !"+Main.c);
				cc.setHoverEvent(se.getHover());
				ServerUtils.permMsg("sanctions.notify.unmute", cc.create());

				sender.sendMessage(ChatComponent.create("§c" + se.on + " à été démuté avec succès !"));
			}else sender.sendMessage(ChatComponent.create("§cSyntaxe : /unmute <player>"));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
