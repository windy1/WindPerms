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

@SuppressWarnings("unchecked")
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
