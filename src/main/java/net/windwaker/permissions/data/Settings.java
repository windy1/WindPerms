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
package net.windwaker.permissions.data;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.data.file.FlatFileGroupManager;
import net.windwaker.permissions.data.file.FlatFileUserManager;
import net.windwaker.permissions.data.sql.SQLGroupManager;
import net.windwaker.permissions.data.sql.SQLUserManager;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.yaml.YamlConfiguration;

import java.io.File;
import java.util.logging.Logger;

public class Settings {
	private static final PermissionsLogger logger = Permissions.getLogger();
	private static final YamlConfiguration data = new YamlConfiguration(new File("plugins/Permissions/settings.yml"));
	/*
	public static final ConfigurationHolder SQL_ENABLED = new ConfigurationHolder(false, "sql.enabled");
	public static final ConfigurationHolder SQL_PROTOCOL = new ConfigurationHolder("mysql", "sql.protocol");
	public static final ConfigurationHolder SQL_HOST = new ConfigurationHolder("184.168.194.134", "sql.host");
	public static final ConfigurationHolder SQL_USERNAME = new ConfigurationHolder("w1ndwaker", "sql.username");
	public static final ConfigurationHolder SQL_PASSWORD = new ConfigurationHolder("WalkerCrouse!1", "sql.password");
	*/

	public static void load() {
		try {
			data.load();
			/*
			data.addChild(SQL_ENABLED);
			data.addChild(SQL_PROTOCOL);
			data.addChild(SQL_HOST);
			data.addChild(SQL_USERNAME);
			data.addChild(SQL_PASSWORD);
			data.save();
			*/
		} catch (ConfigurationException e) {
			logger.severe("Failed to load settings file: " + e.getMessage());
		}
	}

	public GroupManager createGroupManager() {
		if (/*SQL_ENABLED.getBoolean()*/ true) {
			return new SQLGroupManager();
		}

		return new FlatFileGroupManager();
	}

	public UserManager createUserManager() {
		if (/*SQL_ENABLED.getBoolean()*/ true) {
			return new SQLUserManager();
		}

		return new FlatFileUserManager();
	}
}
