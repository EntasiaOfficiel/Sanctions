package fr.entasia.sanctions;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.utils.SanctionEntry;
import net.md_5.bungee.api.CommandSender;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Utils {

	public static ArrayList<SanctionEntry> bans = new ArrayList<>();
	public static ArrayList<SanctionEntry> mutes = new ArrayList<>();


	public static int parseTime(String timeStr){
		int time = 0;
		boolean expectNumber = true;
		StringBuilder a1 = new StringBuilder();
		StringBuilder a2 = new StringBuilder();

		char[] array = timeStr.toCharArray();
		try{
			Integer.parseInt(String.valueOf(array[0]));
		}catch(NumberFormatException ignore){
			return 0;
		}
		try{
			Integer.parseInt(String.valueOf(array[array.length-1]));
			return 0;
		}catch(NumberFormatException ignore){
		}

		for(char c : timeStr.toCharArray()){
			if(c>=48&&c<=57){
				if(expectNumber){
					a1.append(c);
				}else{
					try{
						time += Integer.parseInt(a1.toString()) * getMultiplier(a2.toString());
					}catch(NumberFormatException e){
						return 0;
					}
					expectNumber = true;
					a1 = new StringBuilder();
					a1.append(c);
				}
			}else{ // lettre
				if(expectNumber){
					expectNumber = false;
					a2 = new StringBuilder();
					a2.append(c);
				}else {
					a2.append(c);
				}
			}
		}
		if(expectNumber)return 0;
		time += Integer.parseInt(a1.toString()) * getMultiplier(a2.toString());
		return time;
	}


	public static int getMultiplier(String tu){
		int m = 1;
		switch(tu) {
			case "month":
			case "mo":{
				m *= 30;
			}
			case "day":
			case "d":{
				m *= 24;
			}
			case "hours":
			case "hour":
			case "h":{
				m *= 60;
			}
			case "min":
			case "m":{
				m *= 60;
			}
			case "sec":
			case "s":{
				break;
			}
			default:{
				return 0;
			}
		}
		return m;
	}


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

}
