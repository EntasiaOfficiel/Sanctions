package fr.entasia.sanctions.utils;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.Calendar;
import java.util.Date;

public class MuteEntry {

	public int id;

	public String on;
	public String by;

	public byte type;

	public Calendar when;
	public int time;

	public String reason;


	public HoverEvent getHover(){
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, getInfos().create());
	}

	public ChatComponent getInfos(){
		return new ChatComponent(
				"§cInformations sur la sanction : ID: §4"+Integer.toHexString(id).toUpperCase(),
				"§cSanctionné : §8"+ on,
				"§cPar : §8"+by,
				"§cQuand : §8"+formatWhen(),
				"§cTemps : §8"+TextUtils.secondsToTime(time),
				"§cRaison : §8"+reason);
	}

	public String formatWhen(){
		return TextUtils.formatCalendar(when);
	}

	public int remaning(){
		return (int) ((when.getTimeInMillis()/1000+time)-new Date().getTime()/1000);
	}

	public void SQLDelete(){
		Main.sql.fastUpdate("DELETE FROM actuals WHERE `on`=? and type=?", on, type);
	}

}
