package fr.entasia.sanctions.commands;

import fr.entasia.apis.PlayerUtils;
import fr.entasia.sanctions.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TestCmd extends Command {

	public TestCmd() {
		super("stop");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Main.main.getProxy().stop();
	}

}
