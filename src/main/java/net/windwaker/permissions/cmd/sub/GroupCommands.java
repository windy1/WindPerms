/* Copyright (c) 2012 Walker Crouse, <http://windwaker.net/>
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

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.util.MessageUtil.title;
import static net.windwaker.permissions.cmd.CommandUtil.checkPermission;
import static net.windwaker.permissions.cmd.CommandUtil.getBoolean;
import static net.windwaker.permissions.cmd.CommandUtil.getGroup;

public class GroupCommands {
	private final GroupManager groupManager = Permissions.getGroupManager();

	@Command(aliases = {"info", "information"}, usage = "<group>", desc = "Get general info about a group.", min = 1, max = 1)
	public void info(CommandContext args, CommandSource source) throws CommandException {
		Group group = getGroup(args, 0);
		checkPermission(source, "windperms.group.info." + group.getName());
		title(source, group.getName());
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Default: ", ChatStyle.CYAN, group.isDefault());
	}

	@Command(aliases = {"mk", "make", "add", "create"}, usage = "<group>", desc = "Creates a new group.", min = 1, max = 1)
	@CommandPermissions("windperms.group.add")
	public void add(CommandContext args, CommandSource source) throws CommandException {
		String name = args.getString(0);
		groupManager.addGroup(name);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Added group '", name, "'.");
	}

	@Command(aliases = {"rm", "remove", "del", "delete"}, usage = "<group>", desc = "Remove a group.", min = 1, max = 1)
	@CommandPermissions("windperms.group.remove")
	public void remove(CommandContext args, CommandSource source) throws CommandException {
		String name = args.getString(0);
		groupManager.removeGroup(name);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Removed group '", name, "'.");
	}

	@Command(aliases = "set", usage = "<default|inherit|perm> <group> <value...>", desc = "Set a property for a group.", min = 3, max = 4)
	public void set(CommandContext args, CommandSource source) throws CommandException {
		String property = args.getString(0);
		Group group = getGroup(args, 1);
		String groupName = group.getName();
		ChatArguments message = new ChatArguments(ChatStyle.BRIGHT_GREEN);
		if (property.equalsIgnoreCase("default")) {
			checkPermission(source, "windperms.group.set.default." + groupName);
			Boolean def = true;
			if (args.length() == 3) {
				def = getBoolean(args, 2);
			}
			group.setDefault(def);
			message.append("Set default state of group '", groupName, "' to ", def.toString());
		} else if (property.equalsIgnoreCase("inherit")) {
			checkPermission(source, "windperms.group.set.inherit." + groupName);
			Group inherited = getGroup(args, 2);
			Boolean inherit = true;
			if (args.length() == 4) {
				inherit = getBoolean(args, 3);
			}
			group.setInheritedGroup(inherited, inherit);
			message.append("Set the state of group '", groupName, "' inheriting group '", inherited.getName(), "' to ", inherit.toString());
		} else if (property.equalsIgnoreCase("perm")) {
			checkPermission(source, "windperms.group.set.perm." + groupName);
			String node = args.getString(2);
			Boolean state = true;
			if (args.length() == 4) {
				state = getBoolean(args, 3);
			}
			group.setPermission(node, state);
			message.append("Set the state of node '", node, "' to ", state.toString());
		}
		source.sendMessage(message);
	}
}
