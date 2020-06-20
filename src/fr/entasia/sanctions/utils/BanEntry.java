package fr.entasia.sanctions.utils;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.Calendar;
import java.util.Date;

public class BanEntry extends MuteEntry {
	public byte[] ip;

	@Override
	public void SQLDelete() {
		super.SQLDelete();
		Main.sql.fastUpdate("DELETE FROM global.reports WHERE reported = ?", on);
	}

	public ChatComponent genBanReason(){
		return new ChatComponent(
				"§c§m---§c  Tu es banni d'§bEnta§8sia§c ! (pas cool ca)  §c§m---",
				" ",
				"§cPar : §8"+by,
				"§cLe : §8"+formatWhen(),
				"§cExpiration dans : §8"+(time == -1 ? "Indéfini" : TextUtils.secondsToTime(remaning())),
				"§cBanni pour la raison : §8"+reason,
				" ",
				" ",
				"§cUne réclamation à faire ? Contacte nous sur notre Discord :§6 https://discord.gg/fp9PFP9 §c|§6 https://entasia.fr/discord");
	}
}
