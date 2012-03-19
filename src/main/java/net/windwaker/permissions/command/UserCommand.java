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

import java.util.Set;

/**
 * 
 * @author Windwaker
 */
public class UserCommand {
	
	private final UserManager userManager = Permissions.getUserManager();
	private final GroupManager groupManager = Permissions.getGroupManager();
	
	@Command(aliases = {"user", "us"}, desc = "Modify Permissions users.", usage = "<info|set|add|remove|check|help> [group|perm|build|data] [user] [group:groupName|perm:node|bool:build|identifier] [bool|object]", min = 1, max = 5)
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
				
				if (args.getString(1).equalsIgnoreCase("build")) {
					setCanBuild(source, args.getString(2), args.getString(3));
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

					if (args.getString(2).equalsIgnoreCase("data")) {
						checkData(source, args.getString(2), args.getString(3));
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
				
				if (args.getString(1).equalsIgnoreCase("data")) {
					setData(source, args.getString(2), args.getString(3), args.getString(4));
					return;
				}
			}
		}

		// If it reaches the end while parsing, send help.
		PermissionsCommand.printHelp(source);
		throw new CommandException("Check your argyments!");
	}
	
	private void printHelp(CommandSource source) {
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + "Permissions - Users" + ChatColor.WHITE + "] "
		+ ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user help" + ChatColor.BRIGHT_GREEN + " : Shows this menu.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user info <user>" + ChatColor.BRIGHT_GREEN + " : View a users information.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN
		+ "/user set <group|perm|build|data> <user> <true|false|group|permissionNode|dataTag> [true|false|other]" + ChatColor.BRIGHT_GREEN
		+ " : Set various flag for the user.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user <add|remove> <user>" + ChatColor.BRIGHT_GREEN + " : Add or remove a user.");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- " + ChatColor.CYAN + "/user check <perm|data> <user> <permissionNode|dataTag>" + ChatColor.BRIGHT_GREEN
		+ " : Checks various flags for user.");

	}
	
	private void printInfo(CommandSource source, String username) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}
		
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + user.getName() + ChatColor.WHITE + "] "
		+ ChatColor.BRIGHT_GREEN + "----------");
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "None";
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Group: " + ChatColor.CYAN + groupName);
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Can Build: " + ChatColor.CYAN + user.canBuild());
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
	
	private void setCanBuild(CommandSource source, String username, String bool) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}
		
		boolean canBuild = false;
		if (bool.equalsIgnoreCase("true")) {
			canBuild = true;
		}
		
		user.setCanBuild(canBuild);
		String can = canBuild ? "can" : "cannot";
		source.sendMessage(ChatColor.BRIGHT_GREEN + user.getName() + " " + can + " build now!");
	}
	
	private void setData(CommandSource source, String username, String identifier, String value) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}
		
		//user.setMetadata(identifier, new DataValue(value));
		source.sendMessage(ChatColor.BRIGHT_GREEN + username + ": Set " + identifier + " to " + value);
	}
	
	private void checkPermission(CommandSource source, String username, String node) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}
		
		String has = user.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatColor.BRIGHT_GREEN + "User " + username + " " + has + " permission for " + node);
	}
	
	private void checkData(CommandSource source, String username, String tag) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		// TODO: Check data
	}
}
