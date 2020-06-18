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
