package fr.entasia.sanctions.commands;

import fr.entasia.sanctions.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StopCmd extends Command {

	public StopCmd() {
		super("stop");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Main.main.getProxy().stop();
	}

}
