package fr.entasia.sanctions.commands.infos;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Main;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanListCmd extends Command {

	public BanListCmd() {
		super("banlist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.banlist")){
			sender.sendMessage(ChatComponent.create(execBan(sender, args, false)));
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}

	public static String execBan(CommandSender sender, String[] args, boolean silent){
		if(args.length==0)return "§cSyntaxe : /banlist <pseudo> [options]"; // TODO options à faire
		return "A faire...";
	}

	public static UserManager manager = Main.lpAPI.getUserManager();
}
