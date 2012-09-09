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
package net.windwaker.permissions.cmd.sub;

import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.cmd.CommandUtil.getBoolean;
import static net.windwaker.permissions.cmd.CommandUtil.getGroup;
import static net.windwaker.permissions.util.MessageUtil.title;
import static net.windwaker.permissions.cmd.CommandUtil.checkPermission;
import static net.windwaker.permissions.cmd.CommandUtil.getUser;

public class UserCommands {
	private final UserManager userManager = Permissions.getUserManager();

	@Command(aliases = {"info", "information"}, usage = "<user>", desc = "Get general information about a user.", min = 1, max = 1)
	public void info(CommandContext args, CommandSource source) throws CommandException {
		User user = getUser(args, 0);
		String name = user.getName();
		checkPermission(source, "windperms.user.info." + name);
		title(source, name);
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "Error: No group found!";
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Group: ", ChatStyle.CYAN, groupName);
	}

	@Command(aliases = {"mk", "make", "create", "add"}, usage = "<user>", desc = "Add a new user.", min = 1, max = 1)
	@CommandPermissions("windchat.user.add")
	public void add(CommandContext args, CommandSource source) throws CommandException {
		String name = args.getString(0);
		userManager.addUser(name);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Added user '", name, "'.");
	}

	@Command(aliases = {"rm", "remove", "del", "delete"}, usage = "<user>", desc = "Remove a user.", min = 1, max = 1)
	@CommandPermissions("windchat.user.remove")
	public void remove(CommandContext args, CommandSource source) throws CommandException {
		String name = args.getString(0);
		userManager.removeUser(name);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Removed user '", name, "'.");
	}

	@Command(aliases = "set", usage = "<group|perm> <user> <value...>", desc = "Set a property of a user.", min = 3, max = 4)
	public void set(CommandContext args, CommandSource source) throws CommandException {
		String property = args.getString(0);
		User user = getUser(args, 1);
		String name = user.getName();
		ChatArguments message = new ChatArguments(ChatStyle.BRIGHT_GREEN);
		if (property.equalsIgnoreCase("group")) {
			checkPermission(source, "windperms.user.set.group." + name);
			Group group = getGroup(args, 2);
			user.setGroup(group);
			message.append(name, " is now in group '", group.getName(), "'.");
		} else if (property.equalsIgnoreCase("perm")) {
			checkPermission(source, "windperms.user.set.perm." + name);
			Boolean state = true;
			if (args.length() == 4) {
				state = getBoolean(args, 3);
			}
			String node = args.getString(2);
			user.setPermission(node, state);
			message.append("Set state of node '", node, "' to ", state.toString());
		}
		source.sendMessage(message);
	}
}
