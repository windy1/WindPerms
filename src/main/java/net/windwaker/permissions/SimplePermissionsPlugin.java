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

import java.sql.Connection;
import net.windwaker.permissions.api.*;
import net.windwaker.permissions.command.GroupCommand;
import net.windwaker.permissions.command.PermissionsCommand;
import net.windwaker.permissions.command.UserCommand;
import net.windwaker.permissions.data.Settings;
import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.exception.ConfigurationException;

/**
 * Implementation of PermissionsPlugin
 * 
 * @author Windwaker
 */
public class SimplePermissionsPlugin extends PermissionsPlugin {
	private static SimplePermissionsPlugin instance;
	private GroupManager groupManager;
	private UserManager userManager;
	private final PermissionsLogger logger = Permissions.getLogger();
	private Connection connection;
	
	private Settings settings;

	public SimplePermissionsPlugin() {
		instance = this;
	}

	@Override
	public void onLoad() {

		// Set plugin of platform
		Permissions.setPlugin(this);

		// Load data
		settings = new Settings(this.getDataFolder());
		settings.load();
		if (Settings.SQL_ENABLED.getBoolean()) {
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
	    // TODO: Connect to database with SimpleSave by alta189 and I.
	}

	@Override
	public void onDisable() {
		try {
			settings.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
