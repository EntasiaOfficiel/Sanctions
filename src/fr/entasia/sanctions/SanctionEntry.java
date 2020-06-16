package fr.entasia.sanctions;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.Date;

public class SanctionEntry {

	public int id;

	public String banned;
	public String by;

	public long when;
	public int time;

	public String reason;


	public HoverEvent getHover(){
		ChatComponent text = new ChatComponent(
				"§cInformations sur la sanction : (ID:"+id+")",
				"§cBanni : "+banned,
				"§cPar : "+by,
				"§cQuand : "+new Date(when),
				"§cTemps : "+TextUtils.secondsToTime(time),
				"§cRaison : "+reason);
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.create());
	}

}
