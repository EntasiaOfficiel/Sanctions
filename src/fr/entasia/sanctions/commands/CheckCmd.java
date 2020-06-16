package fr.entasia.sanctions.commands;

import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.PlayerUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.SanctionEntry;
import fr.entasia.sanctions.Utils;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class CheckCmd extends Command {

	public CheckCmd() {
		super("check");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.check")){
			if(args.length==1){
				try{
					ResultSet rs;
					if(args[0].contains(".")){
						try{
							InetAddress.getByName(args[0]);
							rs = Main.sql.fastSelectUnsafe("SELECT * FROM actuals WHERE banned=?", args[0]);
							if(rs.next()) {
								sender.sendMessage(ChatComponent.create("§cSanctions sur cette adresse IP :"));
							}else{
								sender.sendMessage(ChatComponent.create("§cAucune sanction active sur cette adresse IP"));
								return;
							}
						}catch (UnknownHostException e){
							sender.sendMessage(ChatComponent.create("§cAdresse IP invalide !"));
							return;
						}
					}else{
						rs = Main.sql.fastSelectUnsafe("SELECT * FROM actuals WHERE banned=?", args[0]);
						if(rs.next()){
							sender.sendMessage(ChatComponent.create("§cSanctions sur ce pseudo:"));
						}else{
							sender.sendMessage(ChatComponent.create("§cAucune sanction active sur ce pseudo !"));
							return;
						}
					}

					SanctionEntry[] list = new SanctionEntry[4];

					do{
						byte a = rs.getByte("type");
						list[a] = new SanctionEntry();
						list[a].banned = rs.getString("banned");
						list[a].by = rs.getString("by");
						list[a].when = rs.getLong("when");
						list[a].time = rs.getInt("time");
						list[a].reason = rs.getString("reason");
					}while(rs.next());

					String[] l = new String[]{"ipban", "ban", "mute", "warn"};

					ChatComponent comp;
					for(int i=0;i<4;i++){
						comp = new ChatComponent("§4- §c"+l[i]);
						comp.setHoverEvent(list[i].getHover());
					}


				}catch(SQLException e){
					e.printStackTrace();
					Main.sql.broadcastError();
					sender.sendMessage(ChatComponent.create("§cUne erreur interne est survenue ! Préviens iTrooz_"));
				}
			}else sender.sendMessage(ChatComponent.create("§cSyntaxe : /check <pseudo/IP>"));


		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static UserManager manager = Main.lpAPI.getUserManager();
}
