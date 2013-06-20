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
import me.windwaker.permissions.api.permissible.Group;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.data.DataValue;
import org.spout.api.exception.CommandException;

import static me.windwaker.permissions.cmd.CommandUtil.*;

public class GroupCommands {
	private final GroupManager groupManager;

	public GroupCommands(WindPerms plugin) {
		groupManager = plugin.getGroupManager();
	}

	@Command(aliases = {"info", "information"}, usage = "<group>", desc = "Get general info about a group.", min = 1, max = 1)
	public void info(CommandSource source, CommandArguments args) throws CommandException {
		Group group = getGroup(groupManager, args, 0);
		assertHasPermission(source, "windperms.group.info." + group.getName());
		source.sendMessage("========== " + group.getName() + " ==========");
		source.sendMessage("Default: " + group.isDefault());
	}

	@Command(aliases = {"mk", "make", "add", "create"}, usage = "<group>", desc = "Creates a new group.", min = 1, max = 1)
	@Permissible("windperms.group.add")
	public void add(CommandSource source, CommandArguments args) throws CommandException {
		String name = args.getString(0);
		groupManager.addGroup(name);
		source.sendMessage("Added group '" + name + "'.");
	}

	@Command(aliases = {"rm", "remove", "del", "delete"}, usage = "<group>", desc = "Remove a group.", min = 1, max = 1)
	@Permissible("windperms.group.remove")
	public void remove(CommandSource source, CommandArguments args) throws CommandException {
		String name = args.getString(0);
		groupManager.removeGroup(name);
		source.sendMessage("Removed group '" + name + "'.");
	}

	@Command(aliases = "set", usage = "<default|inherit|perm> <group> <value...>", desc = "Set a property for a group.", min = 3, max = 4)
	public void set(CommandSource source, CommandArguments args) throws CommandException {
		String property = args.getString(0);
		Group group = getGroup(groupManager, args, 1);
		String groupName = group.getName();
		String message;
		if (property.equalsIgnoreCase("default")) {
			assertHasPermission(source, "windperms.group.set.default." + groupName);
			Boolean def = true;
			if (args.length() == 3) {
				def = getBoolean(args, 2);
			}
			group.setDefault(def);
			message = "Set default state of group '" + groupName + "' to " + def.toString();
		} else if (property.equalsIgnoreCase("inherit")) {
			assertHasPermission(source, "windperms.group.set.inherit." + groupName);
			Group inherited = getGroup(groupManager, args, 2);
			Boolean inherit = true;
			if (args.length() == 4) {
				inherit = getBoolean(args, 3);
			}
			group.setInheritedGroup(inherited, inherit);
			message = "Set the state of group '" + groupName + "' inheriting group '" + inherited.getName() + "' to " + inherit.toString();
		} else if (property.equalsIgnoreCase("perm")) {
			assertHasPermission(source, "windperms.group.set.perm." + groupName);
			String node = args.getString(2);
			Boolean state = true;
			if (args.length() == 4) {
				state = getBoolean(args, 3);
			}
			group.setPermission(node, state);
			message = "Set the state of node '" + node + "' to " + state.toString();
		} else if (property.equalsIgnoreCase("data") || property.equalsIgnoreCase("md") || property.equalsIgnoreCase("metadata")) {
			assertHasPermission(source, "windperms.group.set.metadata." + groupName);
			String key = args.getString(2);
			Object value = args.get(3);
			group.setMetadata(key, value);
			message = "Set the state of data key '" + key + "' to " + value.toString();
		} else {
			throw new CommandException("Unknown argument: " + property);
		}
		source.sendMessage(message);
	}
}
