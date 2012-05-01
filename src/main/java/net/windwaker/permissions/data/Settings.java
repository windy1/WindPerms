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
package net.windwaker.permissions.data;

import java.io.File;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.data.file.FlatFileGroupManager;
import net.windwaker.permissions.data.file.FlatFileUserManager;
import net.windwaker.permissions.data.sql.SQLGroupManager;
import net.windwaker.permissions.data.sql.SQLUserManager;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

/**
 * Represents all the setting specified in 'plugins/Permissions/config.yml'
 * 
 * @author Windwaker
 */
public class Settings extends ConfigurationHolderConfiguration {
	private static final PermissionsLogger logger = Permissions.getLogger();
	public static final ConfigurationHolder SQL_ENABLED = new ConfigurationHolder(false, "sql.enabled");
	public static final ConfigurationHolder SQL_PROTOCOL = new ConfigurationHolder("mysql", "sql.protocol");
	public static final ConfigurationHolder SQL_HOST = new ConfigurationHolder("184.168.194.134", "sql.host");
	public static final ConfigurationHolder SQL_DATABASE_NAME = new ConfigurationHolder("w1ndwaker", "sql.database-name");
	public static final ConfigurationHolder SQL_USERNAME = new ConfigurationHolder("w1ndwaker", "sql.username");
	public static final ConfigurationHolder SQL_PASSWORD = new ConfigurationHolder("WalkerCrouse!1", "sql.password");
	
	public Settings() {
		super(new YamlConfiguration(new File("plugins/Permissions/config.yml")));
	}
	
	@Override
	public void load() {
		try {
			super.load();
		} catch (ConfigurationException e) {
			logger.severe("Failed to load configuration: " + e.getMessage());
		}
	}
	
	/**
	 * Creates a group manager from the specified settings.
	 * 
	 * @return a new GroupManager
	 */
	public GroupManager createGroupManager() {
		if (SQL_ENABLED.getBoolean()) {
			return new SQLGroupManager();
		}

		return new FlatFileGroupManager();
	}

	/**
	 * Creates a user manager from the specified settings.
	 * 
	 * @return a new UserManager 
	 */
	public UserManager createUserManager() {
		if (SQL_ENABLED.getBoolean()) {
			return new SQLUserManager();
		}

		return new FlatFileUserManager();
	}
}
