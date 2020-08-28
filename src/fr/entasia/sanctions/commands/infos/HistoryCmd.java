package fr.entasia.sanctions.commands.infos;

import fr.entasia.apis.other.ChatComponent;
import fr.entasia.apis.utils.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.SanctionTypes;
import fr.entasia.sanctions.utils.MuteEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class HistoryCmd extends Command {

	public HistoryCmd(String... names) {
		super(names[0], null, names);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.history")) {
			if (args.length == 0) sender.sendMessage(ChatComponent.create("§cSyntaxe : /history <pseudo> [options]"));
			else {
				try {
					ResultSet rs = Main.sql.fastSelectUnsafe("SELECT * FROM history WHERE `on`=? ORDER BY `when` ASC", args[0]);
					if (rs.next()) {
						Calendar c;
						ChatComponent cc;
						HoverEvent hover;
						MuteEntry se;
						String a;
						sender.sendMessage(ChatComponent.create("§cSanctions de §8" + args[0] + "§c :"));
						do {
							se = new MuteEntry(); // pas une vraie sanction, juste utilisée comme structure
							se.id = rs.getInt("id");
							se.on = rs.getString("on");
							se.by = rs.getString("by");
							se.when = Calendar.getInstance();
							se.when.setTimeInMillis(rs.getLong("when"));
							se.time = rs.getInt("time");
							se.reason = rs.getString("reason");

							se.type = rs.getByte("type");

							cc = new ChatComponent("§c" + TextUtils.formatCalendar(se.when) + " §8" + SanctionTypes.getByID(se.type)+" §c"+Main.c);
							a = rs.getString("unban_by");
							if(a==null)hover = se.getHover();
							else{
								c = Calendar.getInstance();
								c.setTimeInMillis(rs.getLong("unban_when"));
								hover = se.getHover(a, c, rs.getString("unban_reason"));
							}

							cc.setHoverEvent(hover);

							sender.sendMessage(cc.create());

						} while (rs.next());
					} else sender.sendMessage(ChatComponent.create("§cAucune sanction n'a été trouvée pour ce joueur ):"));
				} catch (SQLException e) {
					e.printStackTrace();
					Main.sql.broadcastError();
					sender.sendMessage(ChatComponent.create("§cUne erreur SQL s'est produite ! Contacte iTrooz_ !"));
				}
			}
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
