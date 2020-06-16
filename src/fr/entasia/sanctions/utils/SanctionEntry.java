package fr.entasia.sanctions.utils;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.Calendar;
import java.util.Date;

public class SanctionEntry {

	public int id;

	public String on;
	public String by;

	public Calendar when;
	public int time;

	public String reason;

	public byte[] ip; // juste pour le ban


	public HoverEvent getHover(){
		ChatComponent text = new ChatComponent(
				"§cInformations sur la sanction : ID: §4"+Integer.toHexString(id).toUpperCase(),
				"§cSanctionné : "+ on,
				"§cPar : "+by,
				"§cQuand : "+formatWhen(),
				"§cTemps : "+TextUtils.secondsToTime(time),
				"§cRaison : "+reason);
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.create());
	}

	public String formatWhen(){
		return get(Calendar.YEAR, 0)+"/"+get(Calendar.MONTH, 1)+"/"+get(Calendar.DAY_OF_MONTH, 0)+" "+get(Calendar.HOUR_OF_DAY, 0)+":"+get(Calendar.MINUTE, 0);
	}

	private String get(int a, int add){
		String b = String.valueOf(when.get(a)+add);
		if(b.length()==1)return "0"+b;
		else return b;
	}

	public String remaning(){
		return TextUtils.secondsToTime((int) ((when.getTimeInMillis()/1000+time)-new Date().getTime()/1000));
	}


}
