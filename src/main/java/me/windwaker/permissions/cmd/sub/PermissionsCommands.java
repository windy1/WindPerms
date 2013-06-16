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

import org.spout.api.Spout;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.exception.CommandException;

public class PermissionsCommands {
	private final WindPerms plugin;

	public PermissionsCommands(WindPerms plugin) {
		this.plugin = plugin;
	}

	@Command(aliases = {"version", "v"}, desc = "Prints the version info")
	@Permissible("windperms.version")
	public void version(CommandSource source, CommandArguments args) throws CommandException {
		source.sendMessage("WindPerms " + plugin.getDescription().getVersion());
		source.sendMessage("Copyright (c) 2012 Walker Crouse, <http://windwaker.net/>");
		source.sendMessage("Powered by Spout " + Spout.getEngine().getVersion() + " (Implementing SpoutAPI " + Spout.getAPIVersion() + ")");
		source.sendMessage("There are " + plugin.getGroupManager().getGroups().size() + " unique groups registered.");
		source.sendMessage("There are " + plugin.getUserManager().getUsers().size() + " unique users registered.");
	}
}
