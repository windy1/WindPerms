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
		logger.info("Loading group data...");
		for (String name : names) {
			/* debug */ logger.info("Loading user " + name); /* debug */
			String path = "groups/" + name;
			Group group = new Group(name);
			group.setDefault(data.getBoolean(path + "/default"));
			group.setCanBuild(data.getBoolean(path + "/build"));
			group.setPerWorld(data.getBoolean(path + "/per-world"));
			
			// Load permissions
			Set<String> nodes = data.getKeys(path + "/permissions");
			for (String node : nodes) {
				/* debug */ logger.info("Loading permission node " + node); /* debug */
				group.setPermission(node, data.getBoolean(path + "/permissions/" + node));
			}
			
			// Load worlds
			Set<String> worldNames = data.getKeys(path + "/per-world/worlds");
			for (String worldName : worldNames) {
				/* debug */ logger.info("Loading world " + worldName); /* debug */
				World world = Spout.getGame().getWorld(worldName);
				if (world != null) {
					continue;
				}

				/* debug */ logger.info("World " + worldName + " added!"); /* debug */
				group.addWorld(world);
			}
		}
		
		// Load inheritance
		/* debug */ logger.info("Loading inheritance..."); /* debug */
		for (Group group : groups) {
			String path = "groups/" + group.getName();
			Set<String> inheritedNames = data.getKeys(path + "/inherited");
			/* debug */ logger.info("Loading inheritance for group " + group.getName()); /* debug */
			for (String inheritedName : inheritedNames) {
				/* debug */ logger.info("Inheriting group " + inheritedName + " for " + group.getName());  /* debug */
				Group inherited = getGroup(inheritedName);
				if (inherited != null) {
					continue;
				}

				/* debug */ logger.info("Group " + inheritedName + " inherited for " + group.getName()); /* debug */
				boolean inherit = data.getBoolean(path + "/inherited/" + inheritedName);
				group.setInheritedGroup(inherited, inherit);
				if (inherit) {
					continue;
				}

				/* debug */ logger.info("Inheriting permissions from " + inheritedName); /* debug */
				Set<Map.Entry<String, Boolean>> nodes = inherited.getPermissions().entrySet();
				for (Map.Entry<String, Boolean> node : nodes) {
					/* debug */ logger.info("Inheriting " + node.getKey()); /* debug */
					if (!group.getPermissions().containsKey(node.getKey())) {
						continue;
					}
					
					/* debug */ logger.info("Inherited " + node.getKey()); /* debug */
					group.setPermission(node.getKey(), node.getValue());
				}
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
