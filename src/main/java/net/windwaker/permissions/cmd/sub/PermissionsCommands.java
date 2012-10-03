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

import net.windwaker.permissions.api.Permissions;

import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import static net.windwaker.permissions.util.MessageUtil.tip;
import static net.windwaker.permissions.util.MessageUtil.title;

public class PermissionsCommands {
	@Command(aliases = {"version", "v"}, desc = "Prints the version info")
	@CommandPermissions("windperms.version")
	public void version(CommandContext args, CommandSource source) throws CommandException {
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "WindPerms ", Permissions.getPlugin().getDescription().getVersion());
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Copyright (c) 2012 Walker Crouse, <http://windwaker.net/>");
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Powered by", ChatStyle.CYAN, " Spout ", Spout.getEngine().getVersion(), ChatStyle.BRIGHT_GREEN, " (Implementing", ChatStyle.CYAN, " SpoutAPI ", Spout.getAPIVersion(), ChatStyle.BRIGHT_GREEN, ")");
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "There are ", Permissions.getGroupManager().getGroups().size(), " unique groups registered.");
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "There are ", Permissions.getUserManager().getUsers().size(), " unique users registered.");
	}
}
