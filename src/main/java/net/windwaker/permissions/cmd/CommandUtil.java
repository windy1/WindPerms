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
package net.windwaker.permissions.cmd;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;

/**
 * Holds cmd nesters and static util methods for cmd handling.
 */
public final class CommandUtil {
	private CommandUtil() {
	}

	/**
	 * Gets a group from the given {@link CommandArguments} and the index to get it from.
	 * @param args
	 * @param index
	 * @return group
	 * @throws CommandException is group is null
	 */
	public static Group getGroup(GroupManager groupManager, CommandArguments args, int index) throws CommandException {
		Group group = groupManager.getGroup(args.getString(index));
		if (group == null) {
			throw new CommandException("Group not found!");
		}
		return group;
	}

	/**
	 * Gets a user from the given {@link CommandArguments} and the index to get it from.
	 * @param args
	 * @param index
	 * @return user
	 * @throws CommandException if user is null
	 */
	public static User getUser(UserManager userManager, CommandArguments args, int index) throws CommandException {
		User user = userManager.getUser(args.getString(index));
		if (user == null) {
			throw new CommandException("User not found!");
		}
		return user;
	}

	/**
	 * Gets a boolean value from the given {@link CommandArguments} and the index to get it from.
	 * @param args
	 * @param index
	 * @return boolean value
	 */
	public static boolean getBoolean(CommandArguments args, int index) throws CommandException {
		return Boolean.valueOf(args.getString(index));
	}

	/**
	 * Checks if the given {@link CommandSource} has permission for the given node.
	 * @param source
	 * @param node
	 * @throws CommandException if source does not have permission
	 */
	public static void assertHasPermission(CommandSource source, String node) throws CommandException {
		if (!source.hasPermission(node)) {
			throw new CommandException("You don't have permission to do that!");
		}
	}
}
