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
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.util.PermissionsLogger;
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.Spout;
import org.spout.api.data.DataValue;
import org.spout.api.geo.World;
import org.spout.api.util.config.Configuration;

import java.io.File;
import java.util.*;

public class FlatFileGroupManager implements GroupManager {
	private final PermissionsLogger logger = Permissions.getLogger();
	private final Configuration data = new Configuration(new File("plugins/Permissions/groups.yml"));
	private final Set<Group> groups = new HashSet<Group>();

	@Override
	public void load() {
		data.load();
		data.setPathSeparator("/");
		Set<String> names = data.getKeys("groups");
		if (!names.isEmpty()) {
			logger.info("Loading group data...");
		}

		// Load groups
		for (String name : names) {

			// Create new group
			String path = "groups/" + name;
			Group group = new Group(name);

			// Turn off autosave for loading.
			group.setAutoSave(false);

			// Set some values.
			group.setDefault(data.getBoolean(path + "/default"));
			group.setPerWorld(data.getBoolean(path + "/per-world"));

			// Load permissions, data, and worlds
			loadPermissions(group);
			loadWorlds(group);
			loadData(group);

			// Turn autosave back on and add the group.
			group.setAutoSave(true);
			groups.add(group);
		}
		
		// Load inheritance - must be loaded after all other groups are loaded.
		for (Group group : groups) {
			loadInheritance(group);
		}

		if (!names.isEmpty()) {
			logger.info("Group data loaded. " + groups.size() + " unique groups loaded!");
		}
	}
	
	private void loadPermissions(Group group) {
		String path = "groups/" + group.getName();
		Set<String> nodes = data.getKeys(path + "/permissions");
		for (String node : nodes) {
			group.setPermission(node, data.getBoolean(path + "/permissions/" + node));
		}
	}
	
	private void loadWorlds(Group group) {
		String path = "groups/" + group.getName();
		List<String> worldNames = data.getStringList(path + "/worlds");
		for (String worldName : worldNames) {
			World world = Spout.getGame().getWorld(worldName);
			if (world != null) {
				group.addWorld(world);
			}
		}
	}
	
	private void loadData(Group group) {
		String path = "groups/" + group.getName();
		Set<String> nodes = data.getKeys(path + "/metadata");
		for (String node : nodes) {
			group.setMetadata(node, data.getValue(path + "/metadata/" + node));
		}
	}
	
	private void loadInheritance(Group group) {
		String path = "groups/" + group.getName();
		Set<String> inheritedNames = data.getKeys(path + "/inherited");
		for (String inheritedName : inheritedNames) {
			Group inherited = getGroup(inheritedName);
			if (inherited != null) {
				group.setInheritedGroup(inherited, data.getBoolean(path + "/inherited/" + inheritedName));
			}
		}
	}

	@Override
	public void saveGroup(Group group) {
		String path = "groups/" + group.getName();
		saveInheritance(group);
		savePermissions(group);
		saveData(group);
		data.setValue(path + "/per-world", group.isPerWorld());
		data.setValue(path + "/per-world", group.getWorlds());
		data.setValue(path + "/default", group.isDefault());
		data.save();
	}
	
	private void saveInheritance(Group group) {
		String path = "groups/" + group.getName();
		Map<Group, Boolean> groupMap = group.getInheritedGroups();
		for (Map.Entry<Group, Boolean> entry : groupMap.entrySet()) {
			data.setValue(path + "/inherited/" + entry.getKey().getName(), entry.getValue());
		}
	}
	
	private void savePermissions(Group group) {
		String path = "groups/" + group.getName();
		Set<Map.Entry<String, Boolean>> perms = group.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm :  perms) {
			data.setValue(path + "/permissions/" + perm.getKey(), perm.getValue());
		}
	}
	
	private void saveData(Group group) {
		String path = "groups/" + group.getName();
		Set<Map.Entry<String, DataValue>> values = group.getMetadataMap().entrySet();
		for (Map.Entry<String, DataValue> value : values) {
			data.setValue(path + "/metadata/" + value.getKey(), value.getValue());
		}
	}
	
	@Override
	public void addGroup(String name) {
		groups.add(new Group(name));
		String path = "groups/" + name;
		data.setValue(path + "/inherited/admin", false);
		data.setValue(path + "/default", false);
		data.setValue(path + "/per-world", false);
		String[] list = {"world"};
		data.setValue(path + "/worlds", Arrays.asList(list));
		data.setValue(path + "/permissions/foo.bar", false);
		data.setValue(path + "/permissions/baz.qux", false);
		data.setValue(path + "/metadata/build", true);
		data.setValue(path + "/metadata/prefix", "");
		data.setValue(path + "/metadata/suffix", "");
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
			if (group.getName().equalsIgnoreCase(name)) {
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
