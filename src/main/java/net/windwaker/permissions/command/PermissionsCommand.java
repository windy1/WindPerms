/**
 * The Permissions project.
 * Copyright (C) 2012 Walker Crouse
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.windwaker.permissions.command;

import net.windwaker.permissions.SimplePermissionsPlugin;
import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

public class PermissionsCommand {
	private final SimplePermissionsPlugin plugin = SimplePermissionsPlugin.getInstance();
	
	@Command(aliases = {"permissions", "pr"}, desc = "General permissions command.", usage = "[help]")
	@CommandPermissions("permissions.command.permissions")
	public void permissions(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 0) {
			printInfo(source);
			return;
		}
		
		if (args.length() == 1) {
			if (args.getString(0).equalsIgnoreCase("help")) {
				printHelp(source);
				return;
			}
		}

		// If it reaches the end while parsing, send help.
		printHelp(source);
		throw new CommandException("Check your arguments!");
	}
	
	private void printInfo(CommandSource source) {
		source.sendMessage(ChatColor.BRIGHT_GREEN + "This server is running Permissions v" + plugin.getDescription().getVersion() + " by Windwaker.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "There are " + plugin.getGroupManager().getGroups().size() + " unique groups registered.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "There are " + plugin.getUserManager().getUsers().size() + " unique users registered.");
	}
	
	public static void printHelp(CommandSource source) {
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + "Permissions" + ChatColor.WHITE + "] "
		+ ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/gr help" + ChatColor.BRIGHT_GREEN + " : View help for modifying groups.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/us help" + ChatColor.BRIGHT_GREEN + " : View help for modifying users.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/pr help" + ChatColor.BRIGHT_GREEN + " : View this menu.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/pr" + ChatColor.BRIGHT_GREEN + " : View information about this plugin.");
	}
}