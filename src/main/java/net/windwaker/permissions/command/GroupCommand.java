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

import java.util.Map;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.permissible.Group;
import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

/**
 * Handles all command starting with 'group'.
 * 
 * @author Windwaker
 */
public class GroupCommand {
	private final GroupManager groupManager = Permissions.getGroupManager();

	@Command(aliases = {"group", "gr"}, desc = "Modifies a group", usage = "<info|set|add|remove|check|help> [inherit|default|perm|build] [group] [bool:build|bool:default|group|perm|identifier] [bool:inherit|bool:permState]", min = 1, max = 5)
	@CommandPermissions("permissions.command.group")
	public void group(CommandContext args, CommandSource source) throws CommandException {
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
				groupManager.addGroup(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Added group " + args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				groupManager.removeGroup(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Removed group " + args.getString(1));
				return;
			}
		}

		if (args.length() == 3) {
			throw new CommandException("Check your argument count!");
		}

		if (args.length() == 4) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("default")) {
					setDefault(source, args.getString(2), args.getString(3));
					return;
				}

				if (args.getString(1).equalsIgnoreCase("inherit")) {
					setInherit(source, args.getString(2), args.getString(3), "true");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("perm")) {
					setPermission(source, args.getString(2), args.getString(3), "true");
					return;
				}
			}

			if (args.getString(0).equalsIgnoreCase("check")) {
				if (args.getString(1).equalsIgnoreCase("inherit")) {
					checkInherit(source, args.getString(2), args.getString(3));
					return;
				}

				if (args.getString(1).equalsIgnoreCase("perm")) {
					checkPermission(source, args.getString(2), args.getString(3));
					return;
				}
			}
		}

		if (args.length() == 5) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("inherit")) {
					setInherit(source, args.getString(2), args.getString(3), args.getString(4));
					return;
				}

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
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + "Permissions - Groups" + ChatColor.WHITE + "] " + ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/group help" + ChatColor.BRIGHT_GREEN + " : Shows this menu.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/group info <user>" + ChatColor.BRIGHT_GREEN + " : Check a users info.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/group set <inherit|default|perm|build> <group> <true|false|inheritedGroup|permissionNode> [true|false|other]" + ChatColor.BRIGHT_GREEN + " : Set various flags for user.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/group <add|remove> <group>" + ChatColor.BRIGHT_GREEN + " : Add or remove a group.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/group check <inherit|perm> <group> <inheritedGroup|permissionNode>" + ChatColor.BRIGHT_GREEN + " : Check various flags.");
	}

	private void printInfo(CommandSource source, String name) throws CommandException {
		Group group = groupManager.getGroup(name);
		if (group == null) {
			throw new CommandException(name + " doesn't exist!");
		}

		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + group.getName() + ChatColor.WHITE + "] " + ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Default: " + ChatColor.CYAN + group.isDefault());
	}

	private void setInherit(CommandSource source, String groupName, String inheritedName, String bool) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " does not exist!");
		}

		Group inherited = groupManager.getGroup(inheritedName);
		if (inherited == null) {
			throw new CommandException(inheritedName + " does not exist!");
		}

		boolean inherit = false;
		if (bool.equalsIgnoreCase("true")) {
			inherit = true;
		}

		group.setInheritedGroup(inherited, inherit);
		String out = inherit ? "does" : "does not";
		source.sendMessage(ChatColor.BRIGHT_GREEN + group.getName() + " now " + out + " inherits " + inherited.getName());
	}

	private void setDefault(CommandSource source, String groupName, String bool) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		boolean def = false;
		if (bool.equalsIgnoreCase("true")) {
			def = true;
		}

		group.setDefault(def);
		source.sendMessage(ChatColor.BRIGHT_GREEN + group.getName() + "'s default state is now set to " + def);
	}

	private void setPermission(CommandSource source, String groupName, String node, String bool) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		boolean state = false;
		if (bool.equalsIgnoreCase("true")) {
			state = true;
		}

		group.setPermission(node, state);
		String has = state ? "has" : "does not have";
		source.sendMessage(ChatColor.BRIGHT_GREEN + group.getName() + " now " + has + " permissions for " + node);
	}

	private void checkInherit(CommandSource source, String groupName, String inheritedName) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		Group inherited = groupManager.getGroup(inheritedName);
		if (inherited == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		Map<Group, Boolean> inheritance = group.getInheritedGroups();
		if (inheritance.containsKey(inherited)) {
			String does = inheritance.get(inherited) ? "does" : "does not";
			source.sendMessage(ChatColor.BRIGHT_GREEN + "Group " + groupName + " " + does + " inherit " + inheritedName);
		} else {
			source.sendMessage(ChatColor.BRIGHT_GREEN + "Group " + inherited + " has not been assigned to group " + groupName);
		}
	}

	private void checkPermission(CommandSource source, String groupName, String node) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		String has = group.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatColor.BRIGHT_GREEN + "Group " + groupName + " " + has + " permissions for " + node);
	}
}
