package fr.entasia.sanctions.utils;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.TextUtils;
import fr.entasia.sanctions.Main;
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

	public String reason = null; // jamais null

	public boolean isValid(){
		return time==-1||remaning()>0;
	}

	public HoverEvent getHover(){
		return getHover(null, null, null);
	}

	public HoverEvent getHover(String unban_by, Calendar unban_when, String unban_reason){
		ChatComponent cc = new ChatComponent(
				"§cInformations sur la sanction : ID: §4"+Integer.toHexString(id).toUpperCase(),
				"§cSanctionné : §8"+ on,
				"§cPar : §8"+by,
				"§cQuand : §8"+TextUtils.formatCalendar(when),
				"§cTemps : §8"+(time==-1 ? "Indéfini" : TextUtils.secondsToTime(time))
				);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(when.getTimeInMillis()+time*1000);
		cc.append("\n§cExpiration le : §8"+TextUtils.formatCalendar(c));
		System.out.println(time);
		if(time!=-1)cc.append("\n§cExpiration dans : §8"+TextUtils.secondsToTime(remaning()));
		cc.append("\n§cRaison : §8"+reason);

		if(unban_by!=null){
			cc.append("\n§cDébanni par : §8" + unban_by);
			cc.append("\n§cDébanni le : §8" + TextUtils.formatCalendar(unban_when));
			cc.append("\n§cDébanni pour raison : §8" + (unban_reason == null ? "§cIndéfinie" : unban_reason));
		}
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, cc.create());
	}

	public int remaning(){
		return (int) ((when.getTimeInMillis()/1000+time)-new Date().getTime()/1000);
	}

//	public int remaning(){
//		return (int) ((when.getTimeInMillis()/1000+time)-new Date().getTime()/1000);
//	}

	public void SQLDelete(){
		Main.sql.fastUpdate("DELETE FROM actuals WHERE `on`=? and type=?", on, type);
	}

}
