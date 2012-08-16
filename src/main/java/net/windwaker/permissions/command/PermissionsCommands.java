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

import net.windwaker.permissions.WindPerms;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

/**
 * Handles all commands starting with 'user'.
 * @author Windwaker
 */
public class PermissionsCommands {
	private final UserManager userManager = Permissions.getUserManager();
	private final GroupManager groupManager = Permissions.getGroupManager();

	@Command(aliases = {"-user", "-u"}, desc = "Modify Permissions users.", usage = "<info|set|add|remove|check|help> [group|perm|build] [user] [group:groupName|perm:node|bool:build|identifier] [bool]", min = 0, max = 5)
	@CommandPermissions("permissions.command.user")
	public void user(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 1) {
			if (args.getString(0).equalsIgnoreCase("help")) {
				printUserHelp(source);
				return;
			}
		}

		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {
				printUserInfo(source, args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				userManager.addUser(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN + "Added user " + args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				userManager.removeUser(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN + "Removed user " + args.getString(1));
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
					setUserPermission(source, args.getString(2), args.getString(3), "true");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("check")) {
					if (args.getString(2).equalsIgnoreCase("perm")) {
						checkUserPermission(source, args.getString(2), args.getString(3));
						return;
					}
				}
			}
		}

		if (args.length() == 5) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("perm")) {
					setUserPermission(source, args.getString(2), args.getString(3), args.getString(4));
					return;
				}
			}
		}

		// If it reaches the end while parsing, send help.
		printUserHelp(source);
		throw new CommandException("Check your arguments!");
	}

	private void printUserHelp(CommandSource source) {
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "----------" + ChatStyle.WHITE + " [" + ChatStyle.CYAN + "Permissions - Users" + ChatStyle.WHITE + "] " + ChatStyle.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user" + ChatStyle.BRIGHT_GREEN + " : Shows this menu.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user info <user>" + ChatStyle.BRIGHT_GREEN + " : View a users information.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user set <group|perm|build> <user> <true|false|group|permissionNode> [true|false|other]" + ChatStyle.BRIGHT_GREEN + " : Set various flag for the user.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user <add|remove> <user>" + ChatStyle.BRIGHT_GREEN + " : Add or remove a user.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user check <perm> <user> <permissionNode>" + ChatStyle.BRIGHT_GREEN + " : Checks various flags for user.");
	}

	private void printUserInfo(CommandSource source, String username) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		source.sendMessage(ChatStyle.BRIGHT_GREEN + "----------" + ChatStyle.WHITE + " [" + ChatStyle.CYAN + user.getName() + ChatStyle.WHITE + "] " + ChatStyle.BRIGHT_GREEN + "----------");
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "None";
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- Group: " + ChatStyle.CYAN + groupName);
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN + username + " is now in group: " + group.getName());
	}

	private void setUserPermission(CommandSource source, String username, String node, String bool) throws CommandException {
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN + user.getName() + " now " + has + " permission for " + node);
	}

	private void checkUserPermission(CommandSource source, String username, String node) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		String has = user.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "User " + username + " " + has + " permission for " + node);
	}

	@Command(aliases = {"-group", "-g"}, desc = "Modifies a group", usage = "<info|set|add|remove|check|help> [inherit|default|perm|build] [group] [bool:build|bool:default|group|perm|identifier] [bool:inherit|bool:permState]", min = 0, max = 5)
	@CommandPermissions("permissions.command.group")
	public void group(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 1) {
			if (args.getString(0).equalsIgnoreCase("help")) {
				printGroupHelp(source);
				return;
			}
		}

		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {
				printGroupInfo(source, args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				groupManager.addGroup(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN + "Added group " + args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				groupManager.removeGroup(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN + "Removed group " + args.getString(1));
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
					setGroupPermission(source, args.getString(2), args.getString(3), "true");
					return;
				}
			}

			if (args.getString(0).equalsIgnoreCase("check")) {
				if (args.getString(1).equalsIgnoreCase("inherit")) {
					checkInherit(source, args.getString(2), args.getString(3));
					return;
				}

				if (args.getString(1).equalsIgnoreCase("perm")) {
					checkGroupPermission(source, args.getString(2), args.getString(3));
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
					setGroupPermission(source, args.getString(2), args.getString(3), args.getString(4));
					return;
				}
			}
		}

		// If it reaches the end while parsing, send help.
		printGroupHelp(source);
		throw new CommandException("Check your arguments!");
	}

	private void printGroupHelp(CommandSource source) {
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "----------" + ChatStyle.WHITE + " [" + ChatStyle.CYAN + "Permissions - Groups" + ChatStyle.WHITE + "] " + ChatStyle.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group help" + ChatStyle.BRIGHT_GREEN + " : Shows this menu.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group info <user>" + ChatStyle.BRIGHT_GREEN + " : Check a users info.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group set <inherit|default|perm|build> <group> <true|false|inheritedGroup|permissionNode> [true|false|other]" + ChatStyle.BRIGHT_GREEN + " : Set various flags for user.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group <add|remove> <group>" + ChatStyle.BRIGHT_GREEN + " : Add or remove a group.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group check <inherit|perm> <group> <inheritedGroup|permissionNode>" + ChatStyle.BRIGHT_GREEN + " : Check various flags.");
	}

	private void printGroupInfo(CommandSource source, String name) throws CommandException {
		Group group = groupManager.getGroup(name);
		if (group == null) {
			throw new CommandException(name + " doesn't exist!");
		}

		source.sendMessage(ChatStyle.BRIGHT_GREEN + "----------" + ChatStyle.WHITE + " [" + ChatStyle.CYAN + group.getName() + ChatStyle.WHITE + "] " + ChatStyle.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- Default: " + ChatStyle.CYAN + group.isDefault());
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN + group.getName() + " now " + out + " inherits " + inherited.getName());
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN + group.getName() + "'s default state is now set to " + def);
	}

	private void setGroupPermission(CommandSource source, String groupName, String node, String bool) throws CommandException {
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN + group.getName() + " now " + has + " permissions for " + node);
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
			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Group " + groupName + " " + does + " inherit " + inheritedName);
		} else {
			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Group " + inherited + " has not been assigned to group " + groupName);
		}
	}

	private void checkGroupPermission(CommandSource source, String groupName, String node) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		String has = group.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "Group " + groupName + " " + has + " permissions for " + node);
	}

	@Command(aliases = {"-help", "-h"}, desc = "Prints general help")
	public void help(CommandContext args, CommandSource source) throws CommandException {
		printHelp(source);
	}

	@Command(aliases = {"-version", "-v"}, desc = "Prints the version info")
	public void version(CommandContext args, CommandSource source) throws CommandException {
		printInfo(source);
	}

	private void printInfo(CommandSource source) {
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "WindPerms " + Permissions.getPlugin().getDescription().getVersion());
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "Copyright (c) 2012 Walker Crouse, http://windwaker.net/");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "Powered by Spout " + Spout.getEngine().getVersion() + " (Implementing SpoutAPI " + Spout.getAPIVersion() + " )");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "There are " + Permissions.getGroupManager().getGroups().size() + " unique groups registered.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "There are " + Permissions.getUserManager().getUsers().size() + " unique users registered.");
	}

	private void printHelp(CommandSource source) {
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "----------" + ChatStyle.WHITE + " [" + ChatStyle.CYAN + "Permissions" + ChatStyle.WHITE + "] " + ChatStyle.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -group" + ChatStyle.BRIGHT_GREEN + " : View help for modifying groups.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -user" + ChatStyle.BRIGHT_GREEN + " : View help for modifying users.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -help" + ChatStyle.BRIGHT_GREEN + " : View this menu.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN + "- " + ChatStyle.CYAN + "/pr -version" + ChatStyle.BRIGHT_GREEN + " : View information about this plugin.");
	}
}
