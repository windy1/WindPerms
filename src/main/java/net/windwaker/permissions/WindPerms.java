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
package net.windwaker.permissions;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.cmd.sub.GroupCommands;
import net.windwaker.permissions.cmd.sub.PermissionsCommands;
import net.windwaker.permissions.cmd.sub.UserCommands;
import net.windwaker.permissions.io.Settings;

import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandManager;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.entity.Player;
import org.spout.api.plugin.Plugin;

/**
 * Implementation of PermissionsPlugin
 * @author Windwaker
 */
public class WindPerms extends Plugin {
	private PermissionsHandler handler;
	private Settings settings;
	private GroupManager groupManager;
	private UserManager userManager;

	/**
	 * Loads all data within the plugin.
	 */
	private void load() {
		loadData();
		loadPlayers();
		handler = new PermissionsHandler(this);
	}

	private void loadData() {
		// Create and load data
		settings = new Settings(this);
		settings.load();
		// Create and load group manager
		groupManager = settings.createGroupManager();
		groupManager.load();
		// Create and load user manager
		userManager = settings.createUserManager();
		userManager.load();
	}

	private void loadPlayers() {
		Engine engine = getEngine();
		if (engine instanceof Server) {
			userManager.clear();
			for (Player player : ((Server) engine).getOnlinePlayers()) {
				userManager.addUser(player.getName());
			}
		}
	}

	private void registerCommands() {
		CommandManager cm = Spout.getCommandManager();
		// register group commands
		Command group = cm.getCommand("group").addAlias("g");
		AnnotatedCommandExecutorFactory.create(new GroupCommands(this), group);

		// register main commands
		Command permissions = cm.getCommand("permissions").addAlias("pr", "windperms", "wp");
		AnnotatedCommandExecutorFactory.create(new PermissionsCommands(this), permissions);

		// register user commands
		Command user = cm.getCommand("user").addAlias("u");
		AnnotatedCommandExecutorFactory.create(new UserCommands(this), user);
	}

	/**
	 * Saves all data in the plugin.
	 */
	private void save() {
		// Save all data
		settings.save();
		groupManager.save();
		userManager.save();
	}

	/**
	 * Gets the {@link GroupManager} of WindPerms
	 *
	 * @return group manager
	 */
	public GroupManager getGroupManager() {
		return groupManager;
	}

	/**
	 * Gets the {@link UserManager} of WindPerms
	 *
	 * @return user manager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	@Override
	public void onReload() {
		// Load data directly from disk
		load();
		getLogger().info("WindPerms " + getDescription().getVersion() + " reloaded.");
	}

	@Override
	public void onLoad() {
		// Load data
		load();
	}

	@Override
	public void onEnable() {
		// Register events
		getEngine().getEventManager().registerEvents(handler, this);
		registerCommands();
		getLogger().info("WindPerms " + getDescription().getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		// Save data
		save();
		getLogger().info("WindPerms " + getDescription().getVersion() + " disabled.");
	}
}
