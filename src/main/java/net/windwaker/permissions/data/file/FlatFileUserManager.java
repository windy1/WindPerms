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

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.data.DataValue;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class FlatFileUserManager implements UserManager {
	private final PermissionsLogger logger = Permissions.getLogger();
	private static final YamlConfiguration data = new YamlConfiguration(new File("plugins/Permissions/users.yml"));
	private final Set<User> users = new HashSet<User>();

	@Override
	public void load() {
		try {
			GroupManager groupManager = Permissions.getGroupManager();
			data.load();
			data.setPathSeparator("/");
			Set<String> names = data.getNode("users").getKeys(false);
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

				// Load permissions and data
				loadPermissions(user);
				loadData(user);

				// Load group
				Group group = groupManager.getGroup(data.getNode(path + "/group").getString());
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
		} catch (ConfigurationException e) {
			logger.severe("Failed to load user data: " + e.getMessage());
		}
	}
	
	private void loadPermissions(User user) {
		String path = "users/" + user.getName();
		Set<String> nodes = data.getNode(path + "/permissions").getKeys(false);
		for (String node : nodes) {
			user.setPermission(node, data.getNode(path + "/permissions/" + node).getBoolean());
		}
	}
	
	private void loadData(User user) {
		String path = "users/" + user.getName();
		Set<String> nodes = data.getNode(path + "/metadata").getKeys(false);
		for (String node : nodes) {
			user.setMetadata(node, data.getNode(path + "/metadata/" + node).getValue());
		}
	}

	@Override
	public void saveUser(User user) {
		try {
			String path = "users/" + user.getName();
			savePermissions(user);
			saveData(user);
			String groupName = user.getGroup() != null ? user.getGroup().getName() : "";
			data.getNode(path + "/group").setValue(groupName);
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to save user: " + user.getName() + ": " + e.getMessage());
		}
	}
	
	private void savePermissions(User user) {
		String path = "users/" + user.getName();
		Set<Map.Entry<String, Boolean>> perms = user.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			data.getNode(path + "/permissions/" + perm.getKey()).setValue(perm.getValue());
		}
	}
	
	private void saveData(User user) {
		String path = "users/" + user.getName();
		Set<Map.Entry<String, DataValue>> values = user.getMetadataMap().entrySet();
		for (Map.Entry<String, DataValue> value : values) {
			data.getNode(path + "/metadata/" + value.getKey()).setValue(value.getValue());
		}
	}

	@Override
	public void addUser(String username) {
		try {
			users.add(new User(username));
			String path = "users/" + username;
			data.getNode(path + "/group").setValue("default");
			data.getNode(path + "/permissions/foo.bar").setValue(false);
			data.getNode(path + "/permissions/baz.qux").setValue(false);
			data.getNode(path + "/metadata/build").setValue(true);
			data.getNode(path + "/metadata/prefix").setValue("&f");
			data.getNode(path + "/metadata/suffix").setValue("&f");
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to add user " + username + ": " + e.getMessage());
		}
	}

	@Override
	public void removeUser(String username) {
		try {
			User user = getUser(username);
			if (user == null) {
				return;
			}

			users.remove(user);
			data.getNode("users/" + username).setValue(null);
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to remove user " + username + ": " + e.getMessage());
		}
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
