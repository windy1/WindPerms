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
package net.windwaker.permissions.command.sub;

import java.util.Map;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.util.MessageUtil.title;

/**
 * Handles all commands starting with 'user'.
 * @author Windwaker
 */
public class GroupCommand {
	private final GroupManager groupManager = Permissions.getGroupManager();

	@Command(aliases = {"group", "g"}, desc = "Modifies a group", usage = "<info|set|add|remove|check> [inherit|default|perm|build] [group] [bool:build|bool:default|group|perm|identifier] [bool:inherit|bool:permState]", min = 0, max = 5)
	@CommandPermissions("permissions.command.group")
	public void group(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {
				printGroupInfo(source, args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				groupManager.addGroup(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Added group ", args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				groupManager.removeGroup(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Removed group ", args.getString(1));
				return;
			}
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
		throw new CommandException("Check your arguments! See 'pr help' for help.");
	}

	private void printGroupInfo(CommandSource source, String name) throws CommandException {
		Group group = groupManager.getGroup(name);
		if (group == null) {
			throw new CommandException(name + " doesn't exist!");
		}
		title(source, group.getName());
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Default: ", ChatStyle.CYAN, group.isDefault());
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN, group.getName(), " now ", out, " inherits ", inherited.getName());
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN, group.getName(), "'s default state is now set to ", def);
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN, group.getName(), " now ", has, " permissions for ", node);
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
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Group ", groupName, " ", does, " inherit ", inheritedName);
		} else {
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Group ", inherited, " has not been assigned to group ", groupName);
		}
	}

	private void checkGroupPermission(CommandSource source, String groupName, String node) throws CommandException {
		Group group = groupManager.getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " doesn't exist!");
		}

		String has = group.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Group ", groupName, " ", has, " permissions for ", node);
	}
}
