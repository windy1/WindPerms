/* Copyright (c) 2012 Walker Crouse, http://windwaker.net/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.windwaker.permissions.command;

import net.windwaker.permissions.WindPermsPlugin;

import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

/**
 * Handles all commands starting with 'permissions'
 * @author Windwaker
 */
public class PermissionsCommand {
	private final WindPermsPlugin plugin = WindPermsPlugin.getInstance();

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
		source.sendMessage(ChatColor.BRIGHT_GREEN + "This server is running Permissions b" + plugin.getDescription().getVersion() + " by Windwaker.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "There are " + plugin.getGroupManager().getGroups().size() + " unique groups registered.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "There are " + plugin.getUserManager().getUsers().size() + " unique users registered.");
	}

	public static void printHelp(CommandSource source) {
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + "Permissions" + ChatColor.WHITE + "] " + ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/gr help" + ChatColor.BRIGHT_GREEN + " : View help for modifying groups.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/us help" + ChatColor.BRIGHT_GREEN + " : View help for modifying users.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/pr help" + ChatColor.BRIGHT_GREEN + " : View this menu.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/pr" + ChatColor.BRIGHT_GREEN + " : View information about this plugin.");
	}
}