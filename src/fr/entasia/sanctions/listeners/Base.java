package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.util.Arrays;

public class Base implements Listener {

	public static ChatComponent genBanReason(SanctionEntry se, String exp){
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
		for(SanctionEntry se : Utils.bans){
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
		for(SanctionEntry se : Utils.mutes) {
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

}
