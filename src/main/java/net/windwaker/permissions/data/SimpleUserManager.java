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
import java.util.Set;

/**
 * @author Windwaker
 */
public class SimpleUserManager implements UserManager {

	private static final Configuration data = new Configuration(new File("plugins/Permissions/users.yml"));
	private final Set<User> users = new HashSet<User>();

	public SimpleUserManager() {
		data.load();
		Set<String> names = data.getKeys("users");
		for (String name : names) {
			users.add(new User(name));
		}
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