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
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.data.file.FlatFileGroupManager;
import net.windwaker.permissions.data.file.FlatFileUserManager;
import net.windwaker.permissions.data.sql.SQLGroupManager;
import net.windwaker.permissions.data.sql.SQLUserManager;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;

import java.io.File;

public class Settings {
	private static final Settings settings = new Settings();
	private static final Configuration data = new Configuration(new File("plugins/Permissions/settings.yml"));
	public static final ConfigurationNode DATA_MANAGEMENT = new ConfigurationNode("data-management", "sql");
	public static final ConfigurationNode SQL_PROTOCOL = new ConfigurationNode("sql.protocol", "mysql");
	public static final ConfigurationNode SQL_HOST = new ConfigurationNode("sql.host", "184.168.194.134");
	public static final ConfigurationNode SQL_USERNAME = new ConfigurationNode("sql.username", "w1ndwaker");
	public static final ConfigurationNode SQL_PASSWORD = new ConfigurationNode("sql.password", "WalkerCrouse!1");

	private Settings() {

	}

	public static Settings getInstance() {
		return settings;
	}
	
	public static void init() {
		data.load();
		data.addNodes(DATA_MANAGEMENT, SQL_PROTOCOL, SQL_HOST, SQL_USERNAME, SQL_PASSWORD);
		data.save();
	}
	
	public GroupManager createGroupManager() {
		String data = DATA_MANAGEMENT.getString();
		if (data.equalsIgnoreCase("flat-file")) {
			return new FlatFileGroupManager();
		}
		
		if (data.equalsIgnoreCase("sql")) {
			return new SQLGroupManager();
		}

		return new FlatFileGroupManager();
	}
	
	public UserManager createUserManager() {
		String data = DATA_MANAGEMENT.getString();
		if (data.equalsIgnoreCase("flat-file")) {
			return new FlatFileUserManager();
		}

		if (data.equalsIgnoreCase("sql")) {
			return new SQLUserManager();
		}

		return new FlatFileUserManager();
	}
}
