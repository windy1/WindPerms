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

import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.util.MessageUtil.title;

/**
 * Handles all 'pr user' commands.
 */
public class UserCommand {
	private final UserManager userManager = Permissions.getUserManager();

	@Command(aliases = {"user", "u"}, desc = "Modify Permissions users.", usage = "<info|set|add|remove|check> [group|perm|build] [user] [group:groupName|perm:node|bool:build|identifier] [bool]", min = 0, max = 5)
	@CommandPermissions("permissions.command.user")
	public void user(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("info")) {
				printUserInfo(source, args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("add")) {
				userManager.addUser(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Added user ", args.getString(1));
				return;
			}

			if (args.getString(0).equalsIgnoreCase("remove")) {
				userManager.removeUser(args.getString(1));
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Removed user ", args.getString(1));
				return;
			}
		}

		if (args.length() == 4 && args.getString(0).equalsIgnoreCase("set")) {
			if (args.getString(1).equalsIgnoreCase("group")) {
				setGroup(source, args.getString(2), args.getString(3));
				return;
			}

			if (args.getString(1).equalsIgnoreCase("perm")) {
				setUserPermission(source, args.getString(2), args.getString(3), "true");
				return;
			}

			if (args.getString(1).equalsIgnoreCase("check")) {
				checkUserPermission(source, args.getString(2), args.getString(3));
				return;
			}
		}

		if (args.length() == 5 && args.getString(0).equalsIgnoreCase("set") && args.getString(1).equalsIgnoreCase("perm")) {
			setUserPermission(source, args.getString(2), args.getString(3), args.getString(4));
			return;
		}
		throw new CommandException("Check your arguments! See 'pr help' for help.");
	}

	private void printUserInfo(CommandSource source, String username) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}
		title(source, user.getName());
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "None";
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Group: ", ChatStyle.CYAN, groupName);
	}

	private void setGroup(CommandSource source, String username, String groupName) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		Group group = Permissions.getGroupManager().getGroup(groupName);
		if (group == null) {
			throw new CommandException(groupName + " does not exist!");
		}

		user.setGroup(group);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, username, " is now in group: ", group.getName());
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
		source.sendMessage(ChatStyle.BRIGHT_GREEN, user.getName(), " now ", has, " permission for ", node);
	}

	private void checkUserPermission(CommandSource source, String username, String node) throws CommandException {
		User user = userManager.getUser(username);
		if (user == null) {
			throw new CommandException(username + " does not exist!");
		}

		String has = user.hasPermission(node) ? "has" : "does not have";
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "User ", username, " ", has, " permission for ", node);
	}
}
