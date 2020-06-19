package fr.entasia.sanctions.utils;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
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
}
