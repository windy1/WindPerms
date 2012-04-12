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
