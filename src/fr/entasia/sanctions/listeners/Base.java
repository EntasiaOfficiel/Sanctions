package fr.entasia.sanctions.listeners;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.SanctionEntry;
import fr.entasia.sanctions.Utils;
import io.github.waterfallmc.waterfall.event.ConnectionInitEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Base implements Listener {

	@EventHandler(priority = -120)
	public void ban(PreLoginEvent e){
		String name = e.getConnection().getName();
		for(SanctionEntry se : Utils.bans){
			if(se.banned.equals(name)){
				e.setCancelled(true);
				e.setCancelReason(ChatComponent.create("Tu as été banni !"));
				return;
			}
		}
		name = e.getConnection().getAddress().getAddress().getHostAddress();
		for(SanctionEntry se : Utils.ipbans){
			if(se.banned.equals(name)){
				e.setCancelled(true);
				e.setCancelReason(ChatComponent.create("Tu as été banni IP !"));
				return;
			}
		}

	}

}
