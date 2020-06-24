package fr.entasia.sanctions;

import fr.entasia.apis.socket.SocketClient;
import fr.entasia.sanctions.utils.BanEntry;
import fr.entasia.sanctions.utils.MuteEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class Utils {

	public static ArrayList<BanEntry> bans = new ArrayList<>();
	public static ArrayList<MuteEntry> mutes = new ArrayList<>();

	private static int genID(){
		return Main.random.nextInt(0x7FFFFF);
	}

	public static int requ(int index, String requ, Object... values) throws SQLException { // A FAIRE DANS L'HISTORY
		PreparedStatement ps = Main.sql.connection.prepareStatement(requ);
		for(int i = 0; i < values.length; i++) {
			ps.setObject(i + 2, values[i].toString());
		}
		int id;
		while(true){
			try {
				id = genID();
				ps.setInt(index, id);
				ps.execute();
				return id;
			}catch(SQLException e){
				if(!(e.getMessage().contains("Duplicate")&&e.getMessage().contains("PRIMARY"))){
					throw e;
				}
			}
		}
	}

	private static String b64(String a){
		return Base64.getEncoder().encodeToString(a.getBytes());
	}


	public static void sendSancEmbed(MuteEntry se){
		SocketClient.sendData("EBH sanc 0 "+se.type+" "+se.on+" "+se.by+" "+se.when.getTimeInMillis()+" "+se.time+" "+b64(se.reason));
	}

	public static void sendNoSancEmbed(MuteEntry se, String unban_by, String unban_reason){
		SocketClient.sendData("EBH sanc 1 "+se.type+" "+se.on+" "+se.by+" "+se.when.getTimeInMillis()+" "+se.time+" "+b64(se.reason)+" "+unban_by+" "+b64(unban_reason));
	}

	public static void sendModifSancEmbed(MuteEntry se, String modifier, int newTime, String newReason){
		SocketClient.sendData("EBH sanc 2 "+se.type+" "+se.on+" "+se.by+" "+se.when.getTimeInMillis()+" "+se.time+" "+b64(se.reason)+" "+modifier+" "+newTime+" "+b64(newReason));
	}


	/*
	SocketClient.sendData("EBH sanc 0 "+);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Nouvelle sanction :");
		embed.setTimestamp(Instant.now());
		if(kick)embed.addField(":foot: Type", "Kick", false);
		else if(se instanceof BanEntry)embed.addField(":no_entry_sign: Type", "Bannissement", false);
		else embed.addField(":mute: Type", "Mute", false);

		embed.addField(":lock: Staff", se.by, false);
		embed.addField(":no_entry: Sanctionné", se.on, false);
		embed.addField(":question: Raison", se.reason, false);
		if(!kick){
			embed.addField(":alarm_clock: Durée", TextUtils.secondsToTime(se.time), false);
		}
		JDABot.ch_sanctions.sendMessage(embed.build());
	}

	public static void sendNoSancEmbed(MuteEntry se, String unban_by, String unban_reason){
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Sanction annulée :");
		embed.setTimestamp(Instant.now());


		embed.addField(":lock: Staff initial", se.by, false);
		embed.addField(":no_entry: Sanctionné", se.on, false);
		embed.addField(":alarm_clock: Temps restant : ", (se.time == -1 ? "Indeterminé" : TextUtils.secondsToTime(se.remaning())), false);
		embed.addField(":question: Raison initale", se.by, false);
		boolean ban = se instanceof BanEntry;
		embed.addField(":unlock: Staff ayant "+(ban ? "débanni" : "démuté"), unban_by, false);
		embed.addField(":question: Raison de "+(ban ? "unban" : "unmute"), unban_reason, false);
		JDABot.ch_sanctions.sendMessage(embed.build());
	}

	public static void sendModifSancEmbed(MuteEntry se, String unban_by){
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Sanction modifiée :");
		embed.setTimestamp(Instant.now());


		embed.addField(":lock: Staff initial", se.by, false);
		embed.addField(":no_entry: Sanctionné", se.on, false);
		embed.addField(":alarm_clock: Temps initial : ", (se.time == -1 ? "Indeterminé" : TextUtils.secondsToTime(se.remaning())), false);
		embed.addField(":question: Raison initale", se.by, false);
		embed.addField(":unlock: Staff ayant modifié", unban_by, false);
		JDABot.ch_sanctions.sendMessage(embed.build());
	}
	 */

}
