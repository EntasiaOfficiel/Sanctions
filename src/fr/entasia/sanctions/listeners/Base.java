package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Date;

public class Base implements Listener {
	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		byte[] ip = e.getConnection().getAddress().getAddress().getAddress();
		for(SanctionEntry se : Utils.bans){
			if(se.on.equals(name)|| Arrays.equals(ip, se.ip)){
				e.setCancelled(true);
				ChatComponent cc = new ChatComponent(
						"§c§m-----§c  Tu es banni d'§bEnta§7sia§c ! (pas cool ca)  §c§m-----",
						"§cPar : §7"+se.by,
						"§cLe : §7"+se.formatWhen(),
						"§cExpiration dans : §7"+TextUtils.secondsToTime((int) ((se.when.getTimeInMillis()/1000+se.time)-new Date().getTime()/1000)),
						"§cBanni pour la raison : §7"+se.reason,
						" ",
						" ",
						"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord");
				e.setCancelReason(cc.create());

				return;
			}
		}

	}

}
