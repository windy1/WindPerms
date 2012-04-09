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
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.command.GroupCommand;
import net.windwaker.permissions.command.PermissionsCommand;
import net.windwaker.permissions.command.UserCommand;
import net.windwaker.permissions.data.Settings;

import net.windwaker.sql.Connection;
import net.windwaker.sql.Driver;
import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.plugin.PluginManager;

import java.sql.SQLException;

public class SimplePermissionsPlugin extends PermissionsPlugin {
	private static SimplePermissionsPlugin instance;
	private GroupManager groupManager;
	private UserManager userManager;
	private final PermissionsLogger logger = Permissions.getLogger();
	private Connection connection;

	public SimplePermissionsPlugin() {
		instance = this;
	}

	@Override
	public void onLoad() {

		// Set plugin of platform
		Permissions.setPlugin(this);

		// Load data
		Settings settings = new Settings();
		settings.load();
		if (/*Settings.SQL_ENABLED.getBoolean()*/ true) {
			connectToDatabase();
		}
		
		groupManager = settings.createGroupManager();
		groupManager.load();

		userManager = settings.createUserManager();
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
	
	public void connectToDatabase() {
		String host;
		String protocol = null;
		PluginManager pluginManager = Spout.getEngine().getPluginManager();		
		try {
			
			// Create connection
			host = /*Settings.SQL_HOST.getString();*/ "184.168.194.134/w1ndwaker";
			protocol = /*Settings.SQL_PROTOCOL.getString();*/ "mysql";
			connection = new Connection(host, Driver.getByProtocol(protocol));
			
			// Connect
			String user = /*Settings.SQL_USERNAME.getString();*/ "w1ndwaker";
			String password = /*Settings.SQL_PASSWORD.getString();*/ "WalkerCrouse!1";
			logger.info("Connecting to " + host + "...");
			connection.connect(user, password);
			logger.info("Established connection with " + host + "!");

		} catch (SQLException e) {
			logger.severe("Failed to connect to SQL database: " + e.getMessage());
			logger.severe("Shutting down...");
			pluginManager.disablePlugin(this);

		} catch (ClassNotFoundException e) {
			logger.severe("Failed to find valid " + protocol + " JDBC driver: " + e.getMessage());
			logger.severe("Shutting down...");
			pluginManager.disablePlugin(this);

		} catch (InstantiationException e) {
			logger.severe("Failed to find valid " + protocol + " JDBC driver: " + e.getMessage());
			logger.severe("Shutting down...");
			pluginManager.disablePlugin(this);
		
		} catch (IllegalAccessException e) {
			logger.severe("Access is denied to JDBC driver: " + e.getMessage());
			logger.severe("Shutting down...");
			pluginManager.disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		logger.info("Permissions v" + getDescription().getVersion() + " disabled.");
	}

	public static SimplePermissionsPlugin getInstance() {
		return instance;
	}
	
	public Connection getConnection() {
		return connection;
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
