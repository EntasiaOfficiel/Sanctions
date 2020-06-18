package fr.entasia.sanctions.commands.infos;

import com.mysql.jdbc.ResultSetImpl;
import fr.entasia.apis.ChatComponent;
import fr.entasia.apis.TextUtils;
import fr.entasia.sanctions.Main;
import fr.entasia.sanctions.utils.SanctionEntry;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class HistoryCmd extends Command {

	public HistoryCmd() {
		super("history");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.history")) {
			if (args.length == 0) ChatComponent.create("§cSyntaxe : /history <pseudo> [options]"); // TODO options à faire
			else {
				try {

					ResultSet rs = Main.sql.fastSelectUnsafe("SELECT * FROM history WHERE `on`=? ORDER BY `when` ASC", args[0]);
					if (rs.next()) {
						ChatComponent cc;
						ChatComponent h;
						SanctionEntry se;
						String a;
						sender.sendMessage(ChatComponent.create("§cSanctions de §8" + args[0] + "§c :"));
						do {
							se = new SanctionEntry(); // pas une vraie sanction, juste utilisée comme structure
							se.id = rs.getInt("id");
							se.on = rs.getString("on");
							se.by = rs.getString("by");
							se.when = Calendar.getInstance();
							se.when.setTimeInMillis(rs.getLong("when"));
							se.time = rs.getInt("time");
							se.reason = rs.getString("reason");

							cc = new ChatComponent("§c" + TextUtils.formatCalendar(se.when) + " §8" + (rs.getByte("type") == 0 ? "Ban" : "Mute"));
							h = se.getInfos();
							a = rs.getString("unban_by");
							if (a != null) {
								h.append("\n§cDébanni par : §8" + a);
								se.when.setTimeInMillis(rs.getLong("unban_when"));
								h.append("\n§cDébanni le : §8" + TextUtils.formatCalendar(se.when));
								a = rs.getString("unban_reason");
								h.append("\n§cDébanni pour raison : §8" + (a == null ? "§cIndéfinie" : a));
							}

							se.type = rs.getByte("type");
							cc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h.create()));

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
