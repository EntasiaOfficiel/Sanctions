package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.Arrays;

public class Base implements Listener {

	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		byte[] ip = e.getConnection().getAddress().getAddress().getAddress();
		for(SanctionEntry se : Utils.bans){
			if(name.equals(se.on)|| Arrays.equals(ip, se.ip)){
				e.setCancelled(true);
				ChatComponent cc = new ChatComponent(
						"§c§m----§c  Tu es banni d'§bEnta§7sia§c ! (pas cool ca)  §c§m----",
						" ",
						"§cPar : §7"+se.by,
						"§cLe : §7"+se.formatWhen(),
						"§cExpiration dans : §7"+se.remaning(),
						"§cBanni pour la raison : §7"+se.reason,
						" ",
						" ",
						"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord");
				e.setCancelReason(cc.create());

				return;
			}
		}
	}

	@EventHandler(priority = -120)
	public void ban(ChatEvent e){
		String name = ((ProxiedPlayer) e.getSender()).getName();
		for(SanctionEntry se : Utils.mutes) {
			if (name.equals(se.on)){
				e.setCancelled(true);
				((ProxiedPlayer) e.getSender()).sendMessage(ChatComponent.create("§cTu es encore muté pour "+se.remaning()+" !"));
				return;
			}
		}

	}

}
