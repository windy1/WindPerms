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
package net.windwaker.permissions.data.file;

import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.spout.api.util.config.Configuration;

public class FlatFileUserManager implements UserManager {
	private final PermissionsLogger logger = PermissionsLogger.getInstance();
	private static final Configuration data = new Configuration(new File("plugins/Permissions/users.yml"));
	private final Set<User> users = new HashSet<User>();

	@Override
	public void load() {
		GroupManager groupManager = Permissions.getGroupManager();
		data.load();
		data.setPathSeparator("/");
		Set<String> names = data.getKeys("users");
		if (!names.isEmpty()) {
			logger.info("Loading user data...");
		}

		// Load users
		for (String name : names) {

			// Create new user
			String path = "users/" + name;
			User user = new User(name);
			
			// Turn off autosaving for the user while loading - data will not save to disk.
			user.setAutoSave(false);
			
			// Set build
			user.setCanBuild(data.getBoolean(path + "/build"));

			// Load permissions
			loadPermissions(user);

			// Load group
			Group group = groupManager.getGroup(data.getString(path + "/group"));
			if (group != null) {
				user.setGroup(group);
			}

			// Turn autosave back on and add user.
			user.setAutoSave(true);
			users.add(user);
		}
		
		if (!names.isEmpty()) {
			logger.info("User data loaded. " + users.size() + " unique users loaded!");
		}
	}
	
	private void loadPermissions(User user) {
		String path = "users/" + user.getName();
		Set<String> nodes = data.getKeys(path + "/permissions");
		for (String node : nodes) {
			user.setPermission(node, data.getBoolean(path + "/permissions/" + node));
		}
	}

	@Override
	public void saveUser(User user) {
		String path = "users/" + user.getName();
		savePermissions(user);
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "";
		data.setValue(path + "/group", groupName);
		data.setValue(path + "/build", user.canBuild());
		data.save();
	}
	
	private void savePermissions(User user) {
		String path = "users/" + user.getName();
		Set<Map.Entry<String, Boolean>> perms = user.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			data.setValue(path + "/permissions/" + perm.getKey(), perm.getValue());
		}
	}

	@Override
	public void addUser(String username) {
		users.add(new User(username));
		String path = "users/" + username;
		data.setValue(path + "/group", "default");
		data.setValue(path + "/permissions/foo.bar", false);
		data.setValue(path + "/permissions/baz.qux", false);
		data.setValue(path + "/build", true);
		data.setValue(path + "/metadata/prefix", "");
		data.setValue(path + "/metadata/suffix", "");
		data.save();
	}

	@Override
	public void removeUser(String username) {
		User user = getUser(username);
		if (user == null) {
			return;
		}

		users.remove(user);
		data.setValue("users/" + username, null);
		data.save();
	}

	@Override
	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().equals(name)) {
				return user;
			}
		}

		return null;
	}

	@Override
	public Set<User> getUsers() {
		return users;
	}
}
