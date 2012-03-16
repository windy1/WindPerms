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

import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.User;
import org.spout.api.util.config.Configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Windwaker
 */
public class SimpleUserManager implements UserManager {

	private static final Configuration data = new Configuration(new File("plugins/Permissions/users.yml"));
	private final Set<User> users = new HashSet<User>();

	public void load() {
		data.load();
		Set<String> names = data.getKeys("users");
		for (String name : names) {
			users.add(new User(name));
		}
	}

	@Override
	public void saveUser(User user) {

		// Save permissions
		String path = "users." + user.getName();
		Set<Map.Entry<String, Boolean>> perms = user.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			data.setValue(path + ".permissions." + perm.getKey(), perm.getValue());
		}

		// Save data
		/*
		Set<Map.Entry<String, DataValue>> meta = user.getMetadata().entrySet();
		for (Map.Entry<String, DataValue> d : meta) {
			data.setValue(path + ".metadata." + d.getKey(), d.getValue());
		}*/

		// Save misc values
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "";
		data.setValue(path + ".group", groupName);
		data.setValue(path + ".build", user.canBuild());
		data.save();
	}

	@Override
	public void addUser(String username) {
		users.add(new User(username));
		String path = "users." + username;
		data.setValue(path + ".group", "default");
		data.setValue(path + ".permissions.foo", false);
		data.setValue(path + ".permissions.bar", false);
		data.setValue(path + ".build", true);
		data.setValue(path + ".metadata.prefix", "");
		data.setValue(path + ".metadata.suffix", "");
		data.save();
	}

	@Override
	public void removeUser(String username) {
		User user = getUser(username);
		if (user == null) {
			return;
		}

		users.remove(user);
		data.setValue("users." + username, null);
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
