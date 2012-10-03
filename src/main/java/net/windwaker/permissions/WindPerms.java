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
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.PermissionsPlugin;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.cmd.CommandUtil;
import net.windwaker.permissions.data.Settings;

import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;

/**
 * Implementation of PermissionsPlugin
 * @author Windwaker
 */
public class WindPerms extends PermissionsPlugin {
	/**
	 * The instance of the logger
	 */
	private final PermissionsLogger logger = Permissions.getLogger();
	/**
	 * Represents the general settings of the plugin.
	 */
	private Settings settings;
	/**
	 * Represents the {@link GroupManager} of the plugin.
	 */
	private GroupManager groupManager;
	/**
	 * Represents the {@link UserManager} of the plugin.
	 */
	private UserManager userManager;

	@Override
	public void onReload() {
		settings.load();
		groupManager.load();
		userManager.load();
	}

	@Override
	public void onEnable() {
		// Set plugin of platform
		Permissions.setPlugin(this);
		// Load data
		settings = new Settings();
		settings.load();
		// Create group manager
		groupManager = settings.createGroupManager();
		groupManager.load();
		// Create user manager
		userManager = settings.createUserManager();
		userManager.load();
		// Register events
		Spout.getEventManager().registerEvents(new PermissionsHandler(), this);
		// Register commands
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(), new SimpleAnnotatedCommandExecutorFactory());
		getEngine().getRootCommand().addSubCommands(this, CommandUtil.class, commandRegFactory);
		logger.info("WindPerms " + getDescription().getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		logger.info("WindPerms " + getDescription().getVersion() + " disabled.");
	}

	@Override
	public GroupManager getGroupManager() {
		return groupManager;
	}

	@Override
	public UserManager getUserManager() {
		return userManager;
	}
}
