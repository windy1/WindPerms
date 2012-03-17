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
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.util.config.Configuration;

import java.io.File;
import java.util.*;

/**
 * @author Windwaker
 */
public class SimpleGroupManager implements GroupManager {

	private final Logger logger = Logger.getInstance();
	private final Configuration data = new Configuration(new File("plugins/Permissions/groups.yml"));
	private final Set<Group> groups = new HashSet<Group>();

	public void load() {
		data.load();
		data.setPathSeperator("/");
		Set<String> names = data.getKeys("groups");
		if (names.isEmpty()) {
			return;
		}

		logger.info("Loading group data...");
		for (String name :  names) {
			String path = "group/" + name;
			Group group = new Group(name);
			group.setDefault(data.getBoolean(path + "/default"));
			group.setCanBuild(data.getBoolean(path + "/build"));
			group.setPerWorld(data.getBoolean(path + "/per-world"));
			
			// Load permissions
			Set<String> nodes = data.getKeys(path + "/permissions");
			for (String node : nodes) {
				group.setPermission(node, data.getBoolean(path + "/permissions/" + node));
			}
			
			// Load worlds
			List<String> worldNames = data.getStringList(path + "/per-world/worlds");
			if (worldNames != null) {
				for (String worldName : worldNames) {
					World world = Spout.getGame().getWorld(worldName);
					if (world != null) {
						continue;
					}
				
					group.addWorld(world);
				}
			}

			// TODO: Load data
			
			groups.add(group);
			System.out.print(group.toString());
		}
		
		// Load inheritance - All groups must be loaded before we can load inheritance ladder.
		for (String name : names) {
			String path = "groups/" + name;
			Group group = getGroup(name);
			if (group != null) {
				continue;
			}

			Set<String> inheritedNames = data.getKeys(path + "/inherited");
			for (String inheritedName : inheritedNames) {
				Group inherited = getGroup(inheritedName);
				if (inherited != null) {
					continue;
				}
				
				group.setInheritedGroup(inherited, data.getBoolean(path + "/inherited/" + inheritedName));
			}
		}

		logger.info("Group data loaded. " + groups.size() + " unique groups loaded!");
	}

	@Override
	public void saveGroup(Group group) {

		// Save inheritance
		String path = "groups/" + group.getName();
		Set<Map.Entry<Group, Boolean>> groups = group.getInheritedGroups().entrySet();
		for (Map.Entry<Group, Boolean> entry : groups) {
			data.setValue(path + "/inherited/" + entry.getKey().getName(), entry.getValue());
		}

		// Save permissions
		Set<Map.Entry<String, Boolean>> perms = group.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm :  perms) {
			data.setValue(path + "/permissions/" + perm.getKey(), perm.getValue());
		}

		// Save data
		/*
		Set<Map.Entry<String, DataValue>> meta = group.getMetadata().entrySet();
		for (Map.Entry<String, DataValue> d : meta) {
			data.setValue(path + ".metadata." + d.getKey(), d.getValue());
		}*/

		// Save misc values
		data.setValue(path + "/per-world", group.isPerWorld());
		data.setValue(path + "/per-world", group.getWorlds());
		data.setValue(path + "/default", group.isDefault());
		data.setValue(path + "/build", group.canBuild());
		data.save();
	}
	
	@Override
	public void addGroup(String name) {
		groups.add(new Group(name));
		String path = "groups/" + name;
		data.setValue(path + "/inherited/admin", false);
		data.setValue(path + "/default", false);
		data.setValue(path + "/per-world", false);
		String[] list = {""};
		data.setValue(path + "/per-world/worlds", Arrays.asList(list));
		data.setValue(path + "/permissions/foo", false);
		data.setValue(path + "/permissions/bar", false);
		data.setValue(path + "/metadata/prefix", "");
		data.setValue(path + "/metadata/suffix", "");
		data.setValue(path + "/build", true);
		data.save();
	}

	@Override
	public void removeGroup(String name) {
		Group group = getGroup(name);
		if (group == null) {
			return;
		}

		groups.remove(group);
		data.setValue("groups/" + name, null);
		data.save();
	}

	@Override
	public Group getGroup(String name) {
		for (Group group : groups) {
			if (group.getName().equals(name)) {
				return group;
			}
		}

		return null;
	}
	
	@Override
	public Set<Group> getGroups() {
		return groups;
	}
}
