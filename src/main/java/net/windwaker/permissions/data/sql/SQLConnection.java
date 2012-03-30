/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
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
	private static final PermissionsLogger logger = PermissionsLogger.getInstance();
	
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
