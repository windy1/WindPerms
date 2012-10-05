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
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.cmd.CommandUtil;
import net.windwaker.permissions.data.Settings;

import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Player;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.Platform;

/**
 * Implementation of PermissionsPlugin
 * @author Windwaker
 */
public class WindPerms extends CommonPlugin {
	private final PermissionsLogger logger = PermissionsLogger.getInstance();
	private PermissionsHandler handler;
	private Settings settings;
	private GroupManager groupManager;
	private UserManager userManager;

	/**
	 * Loads all data within the plugin.
	 */
	public void load() {
		// Create and load data
		settings = new Settings(this);
		settings.load();
		// Create and load group manager
		groupManager = settings.createGroupManager();
		groupManager.load();
		// Create and load user manager
		userManager = settings.createUserManager();
		userManager.load();
		// Load all online players
		Engine engine = getEngine();
		Platform platform = engine.getPlatform();
		if (platform == Platform.SERVER || platform == Platform.PROXY) {
			for (Player player : ((Server) engine).getOnlinePlayers()) {
				userManager.addUser(player.getName());
			}
		}
		// Create new listener
		handler = new PermissionsHandler(this);
		// De-register and register commands
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		getEngine().getRootCommand().removeChildren(this);
		getEngine().getRootCommand().addSubCommands(this, CommandUtil.class, commandRegFactory);
	}

	/**
	 * Saves all data in the plugin.
	 */
	public void save() {
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
		logger.info("WindPerms " + getDescription().getVersion() + " reloaded.");
	}

	@Override
	public void onLoad() {
		// Load data
		load();
	}

	@Override
	public void onEnable() {
		// Register events
		Spout.getEventManager().registerEvents(handler, this);
		logger.info("WindPerms " + getDescription().getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		// Save data
		save();
		logger.info("WindPerms " + getDescription().getVersion() + " disabled.");
	}
}
