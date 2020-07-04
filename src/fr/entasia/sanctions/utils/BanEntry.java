package fr.entasia.sanctions.utils;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.TextUtils;
import fr.entasia.sanctions.Main;

public class BanEntry extends MuteEntry {
	public byte[] ip;

	public ChatComponent genBanReason(){
		return new ChatComponent(
				"§c§m---§c  Tu es banni d'§bEnta§8sia§c ! (pas cool ca)  §c§m---",
				" ",
				"§cPar : §8"+by,
				"§cLe : §8"+TextUtils.formatCalendar(when),
				"§cExpiration dans : §8"+(time == -1 ? "Indéfini" : TextUtils.secondsToTime(remaning())),
				"§cBanni pour la raison : §8"+reason,
				" ",
				" ",
				"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord");
	}
}