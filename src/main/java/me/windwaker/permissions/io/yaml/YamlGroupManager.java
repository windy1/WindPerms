/*
 * Copyright (c) 2012-2013 ${developer}, <http://windwaker.me>
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
package me.windwaker.permissions.io.yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.windwaker.permissions.WindPerms;
import me.windwaker.permissions.api.GroupManager;
import me.windwaker.permissions.api.permissible.Group;
import me.windwaker.permissions.api.permissible.User;
import org.apache.commons.io.FileUtils;

import org.spout.api.Spout;
import org.spout.api.data.DataValue;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

/**
 * Flat-file implementation of GroupManager done in YAML.
 * @author Windwaker
 */
public class YamlGroupManager implements GroupManager {
	private final File file;
	private final YamlConfiguration data;
	private final Set<Group> groups = new HashSet<Group>();
	private final WindPerms plugin;

	public YamlGroupManager(WindPerms plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "groups.yml");
		data = new YamlConfiguration(file);
	}

	@Override
	public void load() {
		try {

			data.load();
			data.setPathSeparator("/");
			if (!data.getNode("groups").isAttached()) {
				// load some defaults if the file is empty
				addDefaults();
			}

			Set<String> names = data.getNode("groups").getKeys(false);
			if (!names.isEmpty()) {
				plugin.getLogger().info("Loading group data...");
			}

			// Load groups
			for (String name : names) {
				loadGroup(name);
			}

			// Load inheritance - must be loaded after all other groups are loaded.
			for (Group group : groups) {
				loadInheritance(group);
			}

			if (!names.isEmpty()) {
				plugin.getLogger().info("Group data loaded. " + groups.size() + " unique groups loaded!");
			}
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to load group data: " + e.getMessage());
		}
	}

	@Override
	public void save() {
		for (Group group : groups) {
			saveGroup(group);
		}
	}

	private void addDefaults() {
		try {
			FileUtils.copyInputStreamToFile(Spout.getFileSystem().getResourceStream("file://WindPerms/groups.yml"), file);
			data.load();
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to add defaults: " + e.getMessage());
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to copy defaults to configuration " + e.getMessage());
		}
	}

	private void loadPermissions(Group group) {
		String path = "groups/" + group.getName();
		Set<String> nodes = data.getNode(path + "/permissions").getKeys(false);
		for (String node : nodes) {
			group.setPermission(node, data.getNode(path + "/permissions/" + node).getBoolean());
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
		// after all groups are inherited, make sure all their data is up to date
		reloadInheritance();
	}

	@Override
	public void saveGroup(Group group) {
		try {
			String path = "groups/" + group.getName();
			saveInheritance(group);
			savePermissions(group);
			saveData(group);
			data.getNode(path + "/default").setValue(group.isDefault());
			data.save();
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to save group " + group.getName() + ": " + e.getMessage());
		}
	}

	@Override
	public void loadGroup(String group) {
		// Create new group
		String path = "groups/" + group;
		Group g = new Group(plugin, group);
		// Turn off auto-save for loading.
		g.setAutoSave(false);
		// Set some values.
		g.setDefault(data.getNode(path + "/default").getBoolean());
		// Load permissions, data, and worlds
		loadPermissions(g);
		loadData(g);
		// Turn auto-save back on and add the group.
		g.setAutoSave(true);
		groups.add(g);
	}

	private void saveInheritance(Group group) {
		String path = "groups/" + group.getName();
		Map<Group, Boolean> groupMap = group.getInheritedGroups();
		for (Map.Entry<Group, Boolean> entry : groupMap.entrySet()) {
			data.getNode(path + "/inherited/" + entry.getKey().getName()).setValue(entry.getValue());
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
	public Group getDefaultGroup() {
		for (Group group : groups) {
			if (group.isDefault()) {
				return group;
			}
		}
		return null;
	}

	@Override
	public void addGroup(String name) {
		try {
			String path = "groups/" + name;
			data.getNode(path + "/default").setValue(false);
			data.save();
			loadGroup(name);
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to add group " + name + ": " + e.getMessage());
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
			plugin.getLogger().severe("Failed to remove group " + name + ": " + e.getMessage());
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

	@Override
	public void reloadInheritance() {
		for (Group group : groups) {
			group.reloadInheritance();
		}
		for (Group group : groups) {
			for (User user : group.getUsers()) {
				user.inherit(group);
			}
		}
	}

	@Override
	public void clear() {
		groups.clear();
	}
}
