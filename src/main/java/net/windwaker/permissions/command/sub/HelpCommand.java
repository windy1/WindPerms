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

import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.util.MessageUtil.tip;
import static net.windwaker.permissions.util.MessageUtil.title;

/**
 * Handles all 'pr help' commands.
 */
public class HelpCommand {
	@Command(aliases = {"help", "?"}, desc = "Prints basic help.", min = 0, max = 2)
	public void help(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 0) {
			title(source, "Permissions");
			tip(source, "See 'pr help <command>' for more information on a specific command.");
			source.sendMessage(ChatStyle.CYAN, "version", ChatStyle.BRIGHT_GREEN, "      View information about this plugin.");
			source.sendMessage(ChatStyle.CYAN, "help", ChatStyle.BRIGHT_GREEN, "         View this menu.");
			source.sendMessage(ChatStyle.CYAN, "user info", ChatStyle.BRIGHT_GREEN, "    View a users information.");
			source.sendMessage(ChatStyle.CYAN, "user set", ChatStyle.BRIGHT_GREEN, "     Set various flags for the user.");
			source.sendMessage(ChatStyle.CYAN, "user add", ChatStyle.BRIGHT_GREEN, "     Add a user.");
			source.sendMessage(ChatStyle.CYAN, "user remove", ChatStyle.BRIGHT_GREEN, "  Remove a user.");
			source.sendMessage(ChatStyle.CYAN, "user check", ChatStyle.BRIGHT_GREEN, "   Checks various flags for user.");
			source.sendMessage(ChatStyle.CYAN, "group info", ChatStyle.BRIGHT_GREEN, "   Check a users info.");
			source.sendMessage(ChatStyle.CYAN, "group set", ChatStyle.BRIGHT_GREEN, "    Set various flags for user.");
			source.sendMessage(ChatStyle.CYAN, "group add", ChatStyle.BRIGHT_GREEN, "    Add a group.");
			source.sendMessage(ChatStyle.CYAN, "group remove", ChatStyle.BRIGHT_GREEN, " Remove a group");
			source.sendMessage(ChatStyle.CYAN, "group check", ChatStyle.BRIGHT_GREEN, "  Check various flags.");
			return;
		}

		if (args.length() == 1) {
			if (args.getString(0).equalsIgnoreCase("version")) {
				tip(source, "Use 'pr version' to view information about this plugin.");
				return;
			}

			if (args.getString(0).equalsIgnoreCase("help")) {
				tip(source, "Use 'pr help' to view the help menu.");
				return;
			}
		}

		if (args.length() == 2) {
			if (args.getString(0).equalsIgnoreCase("user")) {
				if (args.getString(1).equalsIgnoreCase("info")) {
					tip(source, "Use 'pr user info <username>' to see a player's WindPerms profile.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("set")) {
					tip(source, "Set a users group or permission node with the 'pruser set' command.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Use 'pr user set <group|perm> <username> <property> [property]'");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "For example: 'pr user set group W1ndwaker Admin' would put the user 'W1ndwaker in the 'Admin' group.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Or: 'pr user set perm W1ndwaker spout.command.ban true' would give the user 'W1ndwaker' the 'spout.command.ban' permission node.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("add")) {
					tip(source, "Use 'pr user add <username>' to add a new player to WindPerms.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("remove")) {
					tip(source, "Use 'pr user remove <username>' to remove an existing player's WindPerms profile.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("check")) {
					tip(source, "Use 'pr user check <user> <permission_node>' to check whether or not the user has the specified permission node.");
					return;
				}
			}

			if (args.getString(0).equalsIgnoreCase("group")) {
				if (args.getString(1).equalsIgnoreCase("info")) {
					tip(source, "Use 'pr group info <group>' to see a group's WindPerms profile.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("set")) {
					tip(source, "Set a group's inheritance, default status, and permission nodes,  with the 'pr group set' command.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Use 'pr user set <inherit|default|perm> <group> <property> [property]'");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "For example: 'pr group set inherit Admin Mod true' would have the group 'Admin' now inherit the group 'Mod'.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Or: 'pr group set default Guest true' would make the group 'Guest' the default group.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Or: 'pr group set perm Admin spout.command.ban true' would give the group 'Admin' the 'spout.command.ban' permission node.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("add")) {
					tip(source, "Use 'pr group add <group>' to create a new group.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("remove")) {
					tip(source, "Use 'pr group remove <group>' to remove an existing group.");
					return;
				}

				if (args.getString(1).equalsIgnoreCase("check")) {
					tip(source, "Check if a group inherits another group or has a certain permission node with the 'pr group check' command.");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Use 'pr group check <inherit|perm> <group> <inherited_group|permission_node>");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "For example: 'pr group check inherit Admin Mod' would check if the group 'Admin' inherits the group 'Mod'");
					source.sendMessage(ChatStyle.BRIGHT_GREEN, "Or: 'pr -group check perm Admin spout.command.ban' would check if the group 'Admin' has access to the 'spout.command.ban' permission node.");
					return;
				}
			}
		}
		throw new CommandException("Check your arguments! See 'pr help' for help.");
	}
}
