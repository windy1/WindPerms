/*
 * Copyright (c) 2012-2013 ${developer}, <http://windwaker.me>
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
package me.windwaker.permissions.cmd.sub;

import me.windwaker.permissions.WindPerms;
import me.windwaker.permissions.api.GroupManager;
import me.windwaker.permissions.api.UserManager;
import me.windwaker.permissions.api.permissible.Group;
import me.windwaker.permissions.api.permissible.User;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.exception.CommandException;

import static me.windwaker.permissions.cmd.CommandUtil.*;

public class UserCommands {
	private final UserManager userManager;
	private final GroupManager groupManager;

	public UserCommands(WindPerms plugin) {
		userManager = plugin.getUserManager();
		groupManager = plugin.getGroupManager();
	}

	@Command(aliases = {"info", "information"}, usage = "<user>", desc = "Get general information about a user.", min = 1, max = 1)
	public void info(CommandSource source, CommandArguments args) throws CommandException {
		User user = getUser(userManager, args, 0);
		String name = user.getName();
		assertHasPermission(source, "windperms.user.info." + name);
		source.sendMessage("========== " + name + " ==========");
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "Error: No group found!";
		source.sendMessage("Group: " + groupName);
	}

	@Command(aliases = {"mk", "make", "create", "add"}, usage = "<user>", desc = "Add a new user.", min = 1, max = 1)
	@Permissible("windchat.user.add")
	public void add(CommandSource source, CommandArguments args) throws CommandException {
		String name = args.getString(0);
		userManager.addUser(name);
		source.sendMessage("Added user '" + name + "'.");
	}

	@Command(aliases = {"rm", "remove", "del", "delete"}, usage = "<user>", desc = "Remove a user.", min = 1, max = 1)
	@Permissible("windchat.user.remove")
	public void remove(CommandSource source, CommandArguments args) throws CommandException {
		String name = args.getString(0);
		userManager.removeUser(name);
		source.sendMessage("Removed user '" + name + "'.");
	}

	@Command(aliases = "set", usage = "<group|perm|data> <user> <value...>", desc = "Set a property of a user.", min = 3, max = 4)
	public void set(CommandSource source, CommandArguments args) throws CommandException {
		String property = args.getString(0);
		User user = getUser(userManager, args, 1);
		String name = user.getName();
		String message;
		if (property.equalsIgnoreCase("group")) {
			assertHasPermission(source, "windperms.user.set.group." + name);
			Group group = getGroup(groupManager, args, 2);
			user.setGroup(group);
			message = name + " is now in group '" + group.getName() + "'.";
		} else if (property.equalsIgnoreCase("perm")) {
			assertHasPermission(source, "windperms.user.set.perm." + name);
			Boolean state = true;
			if (args.length() == 4) {
				state = getBoolean(args, 3);
			}
			String node = args.getString(2);
			user.setPermission(node, state);
			message = "Set state of node '" + node + "' to " + state.toString();
		} else if (property.equalsIgnoreCase("data") || property.equalsIgnoreCase("md") || property.equalsIgnoreCase("metadata")) {
			assertHasPermission(source, "windperms.user.set.metadata." + name);
			String key = args.getString(2);
			Object value = args.get(3);
			user.setMetadata(key, value);
			message = "Set state of data key '" + key + "' to " + value;
		} else {
			throw new CommandException("Unknown argument: " + property);
		}
		source.sendMessage(message);
	}
}
