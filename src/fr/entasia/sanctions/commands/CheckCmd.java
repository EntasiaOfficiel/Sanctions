package fr.entasia.sanctions.commands;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class CheckCmd extends Command {

	public CheckCmd() {
		super("check");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.check")){
			if(args.length==1){
				ChatComponent comp = new ChatComponent("§4- §cBanni : ");
				boolean nop = true;
				if(args[0].contains(".")){
					try{
						byte[] ip = InetAddress.getByName(args[0]).getAddress();
						sender.sendMessage(ChatComponent.create("§cSanctions actuelles de l'IP "+args[0]+" :"));
						for(SanctionEntry se : Utils.bans) {
							if(Arrays.equals(se.ip, ip)) {
								nop = false;
								comp.append("§cOui "+Main.c);
								comp.setHoverEvent(se.getHover());
								break;
							}
						}
						if(nop)comp.append("§aNon");
						sender.sendMessage(comp.create());
					}catch (UnknownHostException e){
						sender.sendMessage(ChatComponent.create("§cAdresse IP invalide !"));
					}
				}else{
					sender.sendMessage(ChatComponent.create("§cSanctions actuelles du pseudo "+args[0]+" :"));
					for(SanctionEntry se : Utils.bans) {
						if(se.on.equals(args[0])) {
							nop = false;
							comp.append("§cOui "+Main.c);
							comp.setHoverEvent(se.getHover());
							break;
						}
					}
					if(nop)comp.append("§aNon");
					sender.sendMessage(comp.create());

					comp = new ChatComponent("§4- §cMuté : ");
					nop = true;
					for(SanctionEntry se : Utils.mutes) {
						if(se.on.equals(args[0])) {
							nop = false;
							comp.append("§cOui "+Main.c);
							comp.setHoverEvent(se.getHover());
							break;
						}
					}
					if(nop)comp.append("§aNon");
					sender.sendMessage(comp.create());


				}

			}else sender.sendMessage(ChatComponent.create("§cSyntaxe : /check <pseudo/IP>"));


		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}