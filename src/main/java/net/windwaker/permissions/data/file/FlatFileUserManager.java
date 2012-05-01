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
package net.windwaker.permissions.data.file;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.data.DataValue;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

/**
 * Flat-file implementation of UserManager done in YAML.
 * @author Windwaker
 */
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

				// Turn off auto-saving for the user while loading - data will not save to disk.
				user.setAutoSave(false);

				// Load permissions and data
				loadPermissions(user);
				loadData(user);

				// Load group
				Group group = groupManager.getGroup(data.getNode(path + "/group").getString());
				if (group != null) {
					user.setGroup(group);
				}

				// Turn auto-save back on and add user.
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
