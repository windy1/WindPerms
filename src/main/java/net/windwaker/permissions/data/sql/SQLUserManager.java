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
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;
import net.windwaker.sql.Connection;
import net.windwaker.sql.DataType;
import net.windwaker.sql.Table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SQLUserManager implements UserManager {
	private final Connection connection = SimplePermissionsPlugin.getInstance().getConnection();
	private final Table userTable = new Table(connection, "permissions_users");
	private final PermissionsLogger logger = Permissions.getLogger();
	private final Set<User> users = new HashSet<User>();

	@Override
	public void load() {
		try {

			// Create the user table
			logger.info("Loading user data...");
			if (!userTable.exists()) {
				logger.info("Creating user table...");
				Map<String, DataType> columnDataTypeMap = new HashMap<String, DataType>();
				columnDataTypeMap.put("name", new DataType(DataType.TEXT));
				columnDataTypeMap.put("group", new DataType(DataType.TEXT));
				userTable.create(columnDataTypeMap);
				logger.info("User table created!");
			}
			
			Set<String> names = (Set<String>) userTable.values("name");
			for (String name : names) {
				
				// Create our user
				System.out.println(name);
				User user = new User(name);

				// Turn off auto-save for loading
				user.setAutoSave(false);
				
				// Load permissions and data
				loadPermissions(user);
				loadData(user);
				
				// Load group
				GroupManager groupManager = Permissions.getGroupManager();
				Group group = groupManager.getGroup(userTable.getString("group", "name", name));
				if (group != null) {
					user.setGroup(group);
				}

				// Turn auto-save back on and add the user
				user.setAutoSave(true);
				users.add(user);
			}

			logger.info("User data loaded. " + users.size() + " unique users loaded!");
		
		} catch (SQLException e) {
			logger.severe("Failed to load user data: " + e.getMessage());
		}
	}
	
	private void loadPermissions(User user) {
	}
	
	private void loadData(User user) {
	}

	@Override
	public void addUser(String username) {
		try {
			users.add(new User(username));
			userTable.add(new String[] {"name"}, new String[] {username});
		} catch (SQLException e) {
			logger.severe("Failed to add user " + username + ": " + e.getMessage());
		}
	}

	@Override
	public void removeUser(String username) {
		try {
			userTable.remove("name", username);
			for (User user : users) {
				if (user.getName().equalsIgnoreCase(username)) {
					users.remove(user);
				}
			}
		
		} catch (SQLException e) {
			logger.severe("Failed to remove user " + username + ": " + e.getMessage());
		}
	}

	@Override
	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}

		return null;
	}

	@Override
	public Set<User> getUsers() {
		return users;
	}

	@Override
	public void saveUser(User user) {
	}
	
	private void savePermissions(User user) {
	}
	
	private void saveData(User user) {
	}
}
