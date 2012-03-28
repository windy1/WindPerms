/**
 * The Permissions project.
 * Copyright (C) 2012 Walker Crouse
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.windwaker.permissions;

import net.windwaker.permissions.api.*;
import net.windwaker.permissions.command.GroupCommand;
import net.windwaker.permissions.command.PermissionsCommand;
import net.windwaker.permissions.command.UserCommand;
import net.windwaker.permissions.data.SimpleUserManager;
import net.windwaker.permissions.data.SimpleGroupManager;

import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.plugin.CommonPlugin;

public class SimplePermissionsPlugin extends CommonPlugin implements PermissionsPlugin {
	private static SimplePermissionsPlugin instance;
	private final SimpleGroupManager groupManager = new SimpleGroupManager();
	private final SimpleUserManager userManager = new SimpleUserManager();
	private final Logger logger = Logger.getInstance();

	public SimplePermissionsPlugin() {
		instance = this;
	}

	@Override
	public void onLoad() {

		// Set plugin of platform
		Permissions.setPlugin(this);

		// Load data
		groupManager.load();
		userManager.load();
	}
	
	@Override
	public void onEnable() {
		
		// Register events
		Spout.getEventManager().registerEvents(new PermissionsHandler(), this);
		
		// Register commands
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(), new SimpleAnnotatedCommandExecutorFactory());
		getGame().getRootCommand().addSubCommands(this, PermissionsCommand.class, commandRegFactory);
		getGame().getRootCommand().addSubCommands(this, GroupCommand.class, commandRegFactory);
		getGame().getRootCommand().addSubCommands(this, UserCommand.class, commandRegFactory);

		// Hello world!
		logger.info("Permissions v" + getDescription().getVersion() + " enabled!");
	}

	@Override
	public void onDisable() {
		logger.info("Permissions v" + getDescription().getVersion() + " disabled.");
	}

	public static SimplePermissionsPlugin getInstance() {
		return instance;
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
