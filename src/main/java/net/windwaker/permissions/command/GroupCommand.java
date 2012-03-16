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
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

/**
 * 
 * @author Windwaker
 */
public class GroupCommand {
	
	private final GroupManager groupManager = Permissions.getGroupManager();
	
	@Command(aliases = {"group", "gr"}, desc = "Modifies a group", usage = "<info|set|add|remove> [inherit|default|perm|canBuild|data] <group> [bool:canBuild|bool:default|group|perm|identifier] [bool:inherit|bool:permState|object:data]", min = 2, max = 5)
	@CommandPermissions("permissions.command.group")
	public void group(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {					
				printInfo(source, args.getString(1));
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				groupManager.addGroup(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Added group " + args.getString(1));
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				groupManager.removeGroup(args.getString(1));
				source.sendMessage(ChatColor.BRIGHT_GREEN + "Removed group " + args.getString(1));
			}
		}

		if (args.length() == 3) {
			throw new CommandException("Check your argument count!");
		}
		
		if (args.length() == 4) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("default")) {
					setDefault(source, args.getString(2), args.getString(3));
				}

				if (args.getString(1).equalsIgnoreCase("canbuild")) {
					setCanBuild(source, args.getString(2), args.getString(3));
				}
			}
		}
		
		if (args.length() == 5) {
			if (args.getString(0).equalsIgnoreCase("set")) {
				if (args.getString(1).equalsIgnoreCase("inherit")) {
					setInherit(source, args.getString(2), args.getString(3), args.getString(4));
				}
				
				if (args.getString(1).equalsIgnoreCase("perm")) {
					setPermission(source, args.getString(2), args.getString(3), args.getString(4));
				}
				
				if (args.getString(1).equalsIgnoreCase("data")) {
					setData(source, args.getString(2), args.getString(3), args.getString(4));
				}
			}
		}
	}
	
	private void printInfo(CommandSource source, String name) throws CommandException {
		Group group = groupManager.getGroup(name);
		if (group == null) {
			throw new CommandException(name + " doesn't exist!");
		}
		
		source.sendMessage(ChatColor.BRIGHT_GREEN + "----------" + ChatColor.WHITE + " [" + ChatColor.CYAN + group.getName() + ChatColor.WHITE 
		+ "] " + ChatColor.BRIGHT_GREEN + "----------");
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Default: " + ChatColor.CYAN + group.isDefault());
		source.sendMessage(ChatColor.BRIGHT_GREEN + "- Can Build: " + ChatColor.CYAN + group.canBuild());
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
	
	private void setCanBuild(CommandSource source, String groupName, String bool) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}
		
		boolean canBuild = false;
		if (bool.equalsIgnoreCase("true")) {
			canBuild = true;
		}
		
		group.setCanBuild(canBuild);
		String can = canBuild ? "can" : "cannot";
		source.sendMessage(ChatColor.BRIGHT_GREEN + group.getName() + " " + can + " build now");
	}
	
	private void setData(CommandSource source, String groupName, String tag, String object) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}
		
		//group.setMetadata(tag, new DataValue(object));
		source.sendMessage(ChatColor.BRIGHT_GREEN + group.getName() + ": Set " + tag + " to " + object);
	}
}