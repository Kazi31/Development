package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.MessageManager;

/**
 * Local Chat Command
 * <p>Players can use this command to only see the chat sent from players on their current server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class LocalCommand extends Command {

	private static String[] aliases = new String[] {};

	public LocalCommand() {
		super("local", "multichat.chat.mode", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			ChatModeManager.getInstance().setLocal(((ProxiedPlayer)sender).getUniqueId());

			MessageManager.sendMessage(sender, "command_local_enabled_1");
			MessageManager.sendMessage(sender, "command_local_enabled_2");

		} else {
			MessageManager.sendMessage(sender, "command_local_only_players");
		}
	}
}
