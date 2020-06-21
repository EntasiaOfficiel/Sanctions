package fr.entasia.sanctions.commands.infos;

import fr.entasia.apis.ChatComponent;
import fr.entasia.sanctions.Utils;
import fr.entasia.sanctions.utils.MuteEntry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.function.Predicate;

public class MuteListCmd extends Command {

	public MuteListCmd() {
		super("banlist");
	}

	private static final int PER_PAGES = 15;

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("sanctions.use.mutelist")){
			int l;
			if(args.length==0)l = 0;
			else{
				try{
					l = Integer.parseInt(args[0]);
				}catch(NumberFormatException ignore){
					sender.sendMessage(ChatComponent.create("§cCe nombre est invalide !"));
					return;
				}
				Utils.mutes.removeIf(new Predicate<MuteEntry>() {
					int i=0;
					@Override
					public boolean test(MuteEntry se) {
						if(se.isValid()){
							return false;
						}else return true;
					}
				});
			}
		}else sender.sendMessage(ChatComponent.create("§cTu n'as pas accès à cette commande !"));
	}
}
