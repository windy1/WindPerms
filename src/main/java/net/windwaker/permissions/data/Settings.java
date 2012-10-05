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
package net.windwaker.permissions.data;

import java.io.File;

import net.windwaker.permissions.WindPerms;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.data.sql.SqlGroupManager;
import net.windwaker.permissions.data.sql.SqlUserManager;
import net.windwaker.permissions.data.yaml.YamlGroupManager;
import net.windwaker.permissions.data.yaml.YamlUserManager;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

/**
 * Represents all the settings specified in 'plugins/Permissions/config.yml'
 * @author Windwaker
 */
public class Settings extends ConfigurationHolderConfiguration {
	/**
	 * Whether to use the wildcard node ('*') for giving {@link net.windwaker.permissions.api.permissible.Group}s or {@link net.windwaker.permissions.api.permissible.User}s all permissions.
	 */
	public static final ConfigurationHolder WILDCARD_ENABLED = new ConfigurationHolder(true, "wildcard-enabled");
	/**
	 * Whether to use SQL for the {@link GroupManager} and {@link UserManager}
	 */
	public static final ConfigurationHolder SQL_ENABLED = new ConfigurationHolder(false, "sql", "enabled");
	/**
	 * The protocol of the server's SQL database.
	 */
	public static final ConfigurationHolder SQL_PROTOCOL = new ConfigurationHolder("mysql", "sql", "protocol");
	/**
	 * The host of the SQL database.
	 */
	public static final ConfigurationHolder SQL_HOST = new ConfigurationHolder("spout.org", "sql", "host");
	/**
	 * Name of the SQL database
	 */
	public static final ConfigurationHolder SQL_DATABASE_NAME = new ConfigurationHolder("spout", "sql", "database-name");
	/**
	 * Username for the SQL database
	 */
	public static final ConfigurationHolder SQL_USERNAME = new ConfigurationHolder("spouty", "sql", "username");
	/**
	 * Password for the SQL database
	 */
	public static final ConfigurationHolder SQL_PASSWORD = new ConfigurationHolder("unleashtheflow", "sql", "password");

	private final PermissionsLogger logger = PermissionsLogger.getInstance();
	private final WindPerms plugin;

	/**
	 * Constructs a new Settings configuration at 'plugins/WindPerms/config.yml'
	 */
	public Settings(WindPerms plugin) {
		super(new YamlConfiguration(new File(plugin.getDataFolder(), "config.yml")));
		this.plugin = plugin;
	}

	/**
	 * Creates a group manager from the specified settings.
	 *
	 * @return a new GroupManager
	 */
	public GroupManager createGroupManager() {
		if (SQL_ENABLED.getBoolean()) {
			return new SqlGroupManager();
		}
		return new YamlGroupManager(plugin);
	}

	/**
	 * Creates a user manager from the specified settings.
	 *
	 * @return a new UserManager
	 */
	public UserManager createUserManager() {
		if (SQL_ENABLED.getBoolean()) {
			return new SqlUserManager();
		}
		return new YamlUserManager(plugin);
	}

	@Override
	public void load() {
		try {
			super.load();
			super.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to load configuration: " + e.getMessage());
		}
	}

	@Override
	public void save() {
		try {
			super.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to save configuration: " + e.getMessage());
		}
	}
}
