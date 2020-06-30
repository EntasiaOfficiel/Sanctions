package fr.entasia.sanctions.listeners;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.TextUtils;
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

	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		byte[] ip = e.getConnection().getAddress().getAddress().getAddress();
		for(BanEntry se : Utils.bans){
			if(name.equals(se.on)|| (se.ip!=null&&Arrays.equals(ip, se.ip))){
				ChatComponent cc;
				if(se.isValid()) {
					cc = se.genBanReason();
					e.setCancelled(true);
					e.setCancelReason(cc.create());
				}else {
					se.SQLDelete();
					Utils.bans.remove(se);
				}
				return;
			}
		}
	}

	@EventHandler(priority = -120)
	public void mute(ChatEvent e){
		if(e.getMessage().startsWith("/"))return;
		ProxiedPlayer p = (ProxiedPlayer)e.getSender();
		String name = p.getName();
		for(MuteEntry se : Utils.mutes) {
			if (name.equals(se.on)){
				if(se.time==-1)p.sendMessage(ChatComponent.create("§cTu es muté pour une durée indéfinie !"));
				else{
					int rem = se.remaning();
					if(rem>0){
						p.sendMessage(ChatComponent.create("§cTu es encore muté pour §8"+ TextUtils.secondsToTime(rem)+"§c !"));
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
