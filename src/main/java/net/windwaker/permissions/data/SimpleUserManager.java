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

import net.windwaker.permissions.Logger;
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

/**
 * @author Windwaker
 */
public class SimpleUserManager implements UserManager {

	private final Logger logger = Logger.getInstance();
	private static final Configuration data = new Configuration(new File("plugins/Permissions/users.yml"));
	private final Set<User> users = new HashSet<User>();

	public void load() {
		GroupManager groupManager = Permissions.getGroupManager();
		data.load();
		data.setPathSeparator("/");
		Set<String> names = data.getKeys("users");
		if (!names.isEmpty()) {
			logger.info("Loading user data...");
		}

		for (String name : names) {
			/* debug */ logger.info("Loading user " + name); /* debug */
			String path = "users/" + name;
			User user = new User(name);
			user.setAutosave(false);
			user.setCanBuild(data.getBoolean(path + "/build"));
			Group group = groupManager.getGroup(data.getString(path + "/group"));
			if (group != null) {
				/* debug */ logger.info("Group is not null!"); /* debug */
				// TODO: For some reason, the group is being set to ' ' every startup...
				user.setGroup(group);
			}
			
			// Load permissions
			Set<String> nodes = data.getKeys(path + "/permissions");
			for (String node : nodes) {
				/* debug */ logger.info("Loading permission node " + node); /* debug */
				user.setPermission(node, data.getBoolean(path + "/permissions/" + node));
			}

			if (group != null) {
				Set<Map.Entry<String, Boolean>> ns = group.getPermissions().entrySet();
				for (Map.Entry<String, Boolean> n : ns) {
					/* debug */ System.out.println(n); /* debug */
					if (!user.getPermissions().containsKey(n.getKey())) {
						continue;
					}
				
					/* debug */ logger.info("User does not have inherited node " + n.getKey()); /* debug */
					user.setPermission(n.getKey(), n.getValue());
				}
			} else {
				/* debug */ logger.info("Group is null!"); /* debug */
			}

			/* debug */ logger.info("User " + name + " loaded!"); /* debug */
			user.setAutosave(true);
			users.add(user);
			// debug
			System.out.println(user.getPermissions().entrySet());
			System.out.println(user.getGroup());
			// debug
		}
		
		if (!names.isEmpty()) {
			logger.info("User data loaded. " + users.size() + " unique users loaded!");
			/* debug */ System.out.println(users); /* debug */
		}
	}

	@Override
	public void saveUser(User user) {

		// Save permissions
		String path = "users/" + user.getName();
		Set<Map.Entry<String, Boolean>> perms = user.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			data.setValue(path + "/permissions/" + perm.getKey(), perm.getValue());
		}

		// Save data
		/*
		Set<Map.Entry<String, DataValue>> meta = user.getMetadata().entrySet();
		for (Map.Entry<String, DataValue> d : meta) {
			data.setValue(path + ".metadata." + d.getKey(), d.getValue());
		}*/

		// Save misc values
		String groupName = user.getGroup() != null ? user.getGroup().getName() : "";
		data.setValue(path + "/group", groupName);
		data.setValue(path + "/build", user.canBuild());
		data.save();
	}

	@Override
	public void addUser(String username) {
		users.add(new User(username));
		String path = "users/" + username;
		data.setValue(path + "/group", "default");
		data.setValue(path + "/permissions/foo", false);
		data.setValue(path + "/permissions/bar", false);
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
