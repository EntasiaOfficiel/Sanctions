package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.utils.SanctionEntry;
import fr.entasia.sanctions.utils.SanctionTypes;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Date;

public class Base implements Listener {

	private static BaseComponent[] a(SanctionEntry se){
		return new ChatComponent(
				"§c§m-----§c  Tu es banni d'§bEnta§7sia§c ! (pas cool ca)  §c§m-----",
				"§cPar : §7"+se.by,
				"§cLe : §7"+se.formatWhen(),
				"§cExpiration dans : §7"+TextUtils.secondsToTime((int) ((se.when.getTimeInMillis()/1000+se.time)-new Date().getTime()/1000)),
				"§cBanni pour la raison : §7"+se.reason,
				" ",
				" ",
				"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord"

				// started+time - t

		).create();
	}

	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		for(SanctionEntry se : SanctionTypes.BAN.entries){
			if(se.on.equals(name)){
				e.setCancelled(true);
				e.setCancelReason(a(se));
				return;
			}
		}
		name = e.getConnection().getAddress().getAddress().getHostAddress();
		for(SanctionEntry se : SanctionTypes.IPBAN.entries){
			if(se.on.equals(name)){
				e.setCancelled(true);
				e.setCancelReason(a(se));
				return;
			}
		}

	}

}
