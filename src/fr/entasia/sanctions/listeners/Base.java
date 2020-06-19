package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.BanEntry;
import fr.entasia.sanctions.utils.MuteEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;

public class Base implements Listener {

	public static ChatComponent genBanReason(MuteEntry se, String exp){
		return new ChatComponent(
				"§c§m----§c  Tu es banni d'§bEnta§8sia§c ! (pas cool ca)  §c§m----",
				" ",
				"§cPar : §8"+se.by,
				"§cLe : §8"+se.formatWhen(),
				"§cExpiration dans : §8"+exp,
				"§cBanni pour la raison : §8"+se.reason,
				" ",
				" ",
				"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord");
	}

	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		byte[] ip = e.getConnection().getAddress().getAddress().getAddress();
		for(BanEntry se : Utils.bans){
			if(name.equals(se.on)|| Arrays.equals(ip, se.ip)){
				ChatComponent cc;
				if(se.time!=-1){
					int rem = se.remaning();
					if(rem>0) cc = genBanReason(se, TextUtils.secondsToTime(rem));
					else{
						se.SQLDelete();
						Utils.bans.remove(se);
						return;
					}
				}else cc = genBanReason(se, "Indéfini");
				e.setCancelled(true);
				e.setCancelReason(cc.create());
				return;
			}
		}
	}

	@EventHandler(priority = -120)
	public void ban(ChatEvent e){
		if(e.getMessage().startsWith("/"))return;
		ProxiedPlayer p = (ProxiedPlayer)e.getSender();
		String name = p.getName();
		for(MuteEntry se : Utils.mutes) {
			if (name.equals(se.on)){
				if(se.time==-1)p.sendMessage(ChatComponent.create("§cTu es muté pour une durée indéfinie !"));
				else{
					int rem = se.remaning();
					if(rem>0){
						p.sendMessage(ChatComponent.create("§cTu es encore muté pour §8"+ TextUtils.secondsToTime(se.remaning())+"§c !"));
					}else{
						se.SQLDelete();
						Utils.mutes.remove(se);
						return;
					}
				}
				e.setCancelled(true);
				return;
			}
		}
	}


	private boolean isTabCmd(String value) {
		for (String i : Main.cmdcompletes) {
			if (value.equals(i)) return true;
		}
		return false;
	}

	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		String[] args = e.getCursor().split(" ");
		if(args[0].length()<=1)return;
		if(isTabCmd(args[0].substring(1).toLowerCase())){
			if(((ProxiedPlayer)e.getSender()).hasPermission("sanctions.use."+args[0])){
				if(args.length>1){
					args[1] = args[1].toLowerCase();
					for(ProxiedPlayer p : Main.main.getProxy().getPlayers()) {
						if(p.getDisplayName().toLowerCase().startsWith(args[1]))e.getSuggestions().add(p.getDisplayName());
					}
				}else{
					for(ProxiedPlayer p : Main.main.getProxy().getPlayers()) {
						e.getSuggestions().add(p.getDisplayName());
					}
				}
			}
		}
	}

}
