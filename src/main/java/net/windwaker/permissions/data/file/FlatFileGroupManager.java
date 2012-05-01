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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.Spout;
import org.spout.api.data.DataValue;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.geo.World;
import org.spout.api.util.config.yaml.YamlConfiguration;

/**
 * Flat-file implementation of GroupManager done in YAML.
 * @author Windwaker
 */
public class FlatFileGroupManager implements GroupManager {
	private final PermissionsLogger logger = Permissions.getLogger();
	private final YamlConfiguration data = new YamlConfiguration(new File("plugins/Permissions/groups.yml"));
	private final Set<Group> groups = new HashSet<Group>();

	@Override
	public void load() {
		try {
			data.load();
			data.setPathSeparator("/");
			Set<String> names = data.getNode("groups").getKeys(false);
			if (!names.isEmpty()) {
				logger.info("Loading group data...");
			}

			// Load groups
			for (String name : names) {
				// Create new group
				String path = "groups/" + name;
				Group group = new Group(name);

				// Turn off auto-save for loading.
				group.setAutoSave(false);

				// Set some values.
				group.setDefault(data.getNode(path + "/default").getBoolean());
				group.setPerWorld(data.getNode(path + "/per-world").getBoolean());

				// Load permissions, data, and worlds
				loadPermissions(group);
				loadWorlds(group);
				loadData(group);

				// Turn auto-save back on and add the group.
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
		} catch (ConfigurationException e) {
			logger.severe("Failed to load group data: " + e.getMessage());
		}
	}

	private void loadPermissions(Group group) {
		String path = "groups/" + group.getName();
		Set<String> nodes = data.getNode(path + "/permissions").getKeys(false);
		for (String node : nodes) {
			group.setPermission(node, data.getNode(path + "/permissions/" + node).getBoolean());
		}
	}

	private void loadWorlds(Group group) {
		String path = "groups/" + group.getName();
		List<String> worldNames = data.getNode(path + "/worlds").getStringList();
		for (String worldName : worldNames) {
			World world = Spout.getEngine().getWorld(worldName);
			if (world != null) {
				group.addWorld(world);
			}
		}
	}

	private void loadData(Group group) {
		String path = "groups/" + group.getName();
		Set<String> nodes = data.getNode(path + "/metadata").getKeys(false);
		for (String node : nodes) {
			group.setMetadata(node, data.getNode(path + "/metadata/" + node).getValue());
		}
	}

	private void loadInheritance(Group group) {
		String path = "groups/" + group.getName();
		Set<String> inheritedNames = data.getNode(path + "/inherited").getKeys(false);
		for (String inheritedName : inheritedNames) {
			Group inherited = getGroup(inheritedName);
			if (inherited != null) {
				group.setInheritedGroup(inherited, data.getNode(path + "/inherited/" + inheritedName).getBoolean());
			}
		}
	}

	@Override
	public void saveGroup(Group group) {
		try {
			String path = "groups/" + group.getName();
			saveInheritance(group);
			savePermissions(group);
			saveData(group);
			data.getNode(path + "/per-world").setValue(group.isPerWorld());
			data.getNode(path + "/worlds").setValue(group.getWorlds());
			data.getNode(path + "/default").setValue(group.isDefault());
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to save group " + group.getName() + ": " + e.getMessage());
		}
	}

	private void saveInheritance(Group group) {
		String path = "groups/" + group.getName();
		Map<Group, Boolean> groupMap = group.getInheritedGroups();
		for (Map.Entry<Group, Boolean> entry : groupMap.entrySet()) {
			data.getNode(path + "/inherited/" + entry.getKey()).setValue(entry.getValue());
		}
	}

	private void savePermissions(Group group) {
		String path = "groups/" + group.getName();
		Set<Map.Entry<String, Boolean>> perms = group.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			data.getNode(path + "/permissions/" + perm.getKey()).setValue(perm.getValue());
		}
	}

	private void saveData(Group group) {
		String path = "groups/" + group.getName();
		Set<Map.Entry<String, DataValue>> values = group.getMetadataMap().entrySet();
		for (Map.Entry<String, DataValue> value : values) {
			data.getNode(path + "/metadata/" + value.getKey()).setValue(value.getValue());
		}
	}

	@Override
	public void addGroup(String name) {
		try {
			groups.add(new Group(name));
			String path = "groups/" + name;
			data.getNode(path + "/inherited/admin").setValue(false);
			data.getNode(path + "/default").setValue(false);
			data.getNode(path + "/per-world").setValue(false);
			data.getNode(path + "/worlds").setValue(Arrays.asList("world"));
			data.getNode(path + "/permissions/foo.bar").setValue(false);
			data.getNode(path + "/permissions/baz.qux").setValue(false);
			data.getNode(path + "/metadata/build").setValue(true);
			data.getNode(path + "/metadata/prefix").setValue("&f");
			data.getNode(path + "/metadata/suffix").setValue("&f");
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to add group " + name + ": " + e.getMessage());
		}
	}

	@Override
	public void removeGroup(String name) {
		try {
			Group group = getGroup(name);
			if (group == null) {
				return;
			}

			groups.remove(group);
			data.getNode("groups/" + name).setValue(null);
			data.save();
		} catch (ConfigurationException e) {
			logger.severe("Failed to remove group " + name + ": " + e.getMessage());
		}
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
