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
package net.windwaker.permissions.data.sql;

import net.windwaker.permissions.SimplePermissionsPlugin;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.data.Settings;
import org.spout.api.Spout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLConnection {
	private static final SQLConnection instance = new SQLConnection();
	private static final PermissionsLogger logger = Permissions.getLogger();
	
	private SQLConnection() {

	}

	public static SQLConnection getConnection() {
		return instance;
	}

	public static void init() {
		try {

			// Define the protocol and get an instance of the driver.
			String host = Settings.SQL_URI.getString();
			logger.info("Attempting to connect to SQL database at " + host + "...");
			String protocol = Settings.SQL_PROTOCOL.getString();
			Class.forName(getDriver(protocol)).newInstance();

			// Fetch the database info from our configuration.
			String username = Settings.SQL_USERNAME.getString();
			String password = Settings.SQL_PASSWORD.getString();
			String uri = "jdbc:" + protocol + "://" + Settings.SQL_URI.getString() + "/database?user=" + username + "&password=" + password;

			// Connect
			Connection connection = DriverManager.getConnection(uri);
			Statement statement = connection.createStatement();
			logger.info("Connected to SQL database at " + host);
		} catch (Exception e) {
			logger.severe("Failed to connect to SQL database: " + e.getMessage());
			logger.severe("Shutting down...");
			Spout.getGame().getPluginManager().disablePlugin(SimplePermissionsPlugin.getInstance());
		}
	}
	
	private static String getDriver(String name) {
		if (name.equalsIgnoreCase("mysql")) {
			return "com.mysql.jdbc.Driver";
		}

		if (name.equalsIgnoreCase("sqlite")) {
			return "org.sqlite.JDBC";
		}

		if (name.equalsIgnoreCase("postgre")) {
			return "org.postgresql.Driver";
		}

		return null;
	}
}
