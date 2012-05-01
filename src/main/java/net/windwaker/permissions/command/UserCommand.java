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

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

/**
 * Handles all commands starting with 'user'.
 * @author Windwaker
 */
public class UserCommand {
	private final UserManager userManager = Permissions.getUserManager();
	private final GroupManager groupManager = Permissions.getGroupManager();

	@Command(aliases = {"user", "us"}, desc = "Modify Permissions users.", usage = "<info|set|add|remove|check|help> [group|perm|build] [user] [group:groupName|perm:node|bool:build|identifier] [bool]", min = 1, max = 5)
	@CommandPermissions("permissions.command.user")
	public void user(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 1) {
			if (args.getString(0).equalsIgnoreCase("help")) {
				printHelp(source);
				return;
			}
		}

		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {
				printInfo(source, args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				userManager.addUser(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Added user " + args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				userManager.removeUser(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Removed user " + args.getString(1));
				return;
			}
		}

		if (args.length() == 3) {
			throw new CommandException("Check your arguments count!");
		}

		if (args.length() == 4) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("group")) {
					setGroup(source, args.getString(2), args.getString(3));
					return;
				}

				if (args.getString(1).equalsIgnoreCase("perm")) {
					setPermission(source, args.getString(2), args.getString(3), "true");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("check")) {
					if (args.getString(2).equalsIgnoreCase("perm")) {
						checkPermission(source, args.getString(2), args.getString(3));
						return;
					}
				}
			}
		}

		if (args.length() == 5) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("perm")) {
					setPermission(source, args.getString(2), args.getString(3), args.getString(4));
					return;
				}
			}
		}

		// If it reaches the end while parsing, send help.
		PermissionsCommand.printHelp(source);
		throw new CommandException("Check your arguments!");
	}

	private void printHelp(CommandSource source) {
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + "Permissions - Users" + ChatColor.WHITE + "] " + ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user help" + ChatColor.BRIGHT_GREEN + " : Shows this menu.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user info <user>" + ChatColor.BRIGHT_GREEN + " : View a users information.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user set <group|perm|build> <user> <true|false|group|permissionNode> [true|false|other]" + ChatColor.BRIGHT_GREEN + " : Set various flag for the user.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user <add|remove> <user>" + ChatColor.BRIGHT_GREEN + " : Add or remove a user.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user check <perm> <user> <permissionNode>" + ChatColor.BRIGHT_GREEN + " : Checks various flags for user.");
	}

	private void printInfo(CommandSource source, String username) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + user.getName() + ChatColor.WHITE + "] " + ChatColor.BRIGHT_GREEN + "----------");
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "None";
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Group: " + ChatColor.CYAN + groupName);
	}

	private void setGroup(CommandSource source, String username, String groupName) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " does not exist!");
		}

		user.setGroup(group);
		source.sendMessage(ChatColor.BRIGHT_GREEN + username + " is now in group: " + group.getName());
	}

	private void setPermission(CommandSource source, String username, String node, String bool) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		boolean state = false;
		if (bool.equalsIgnoreCase("true")) {
			state = true;
		}

		user.setPermission(node, state);
		String has = state ? "has" : "does not have";
		source.sendMessage(ChatColor.BRIGHT_GREEN + user.getName() + " now " + has + " permission for " + node);
	}

	private void checkPermission(CommandSource source, String username, String node) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		String has = user.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatColor.BRIGHT_GREEN + "User " + username + " " + has + " permission for " + node);
	}
}
