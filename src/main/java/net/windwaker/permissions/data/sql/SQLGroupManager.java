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
package net.windwaker.permissions.data.sql;

import net.windwaker.permissions.SimplePermissionsPlugin;
import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.sql.Connection;
import net.windwaker.sql.DataType;
import net.windwaker.sql.Table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SQLGroupManager implements GroupManager {
	private final Connection connection = SimplePermissionsPlugin.getInstance().getConnection();
	private final Table groupTable = new Table(connection, "permissions_groups");
	private final PermissionsLogger logger = Permissions.getLogger();
	private final Set<Group> groups = new HashSet<Group>();
	
	@Override
	public void load() {
		try {
			
			// Create the group table
			logger.info("Loading group data...");
			if (!groupTable.exists()) {
				logger.info("Creating group table...");
				Map<String, DataType> columnDataTypeMap = new HashMap<String, DataType>();
				columnDataTypeMap.put("name", new DataType(DataType.TEXT));
				columnDataTypeMap.put("def", new DataType(DataType.CHARACTER, "1"));
				columnDataTypeMap.put("per_world", new DataType(DataType.CHARACTER, "1"));
				groupTable.create(columnDataTypeMap);
				logger.info("Group table created!");
			}

			Set<String> names = (Set<String>) groupTable.values("name");
			for (String name : names) {

				// Create our group
				System.out.print("Name: " + name);
				Group group = new Group(name);

				// Turn auto-save off for loading
				group.setAutoSave(false);

				// Set some values
				group.setDefault(groupTable.getBoolean("def", "name", name));
				group.setPerWorld(groupTable.getBoolean("per_world", "name", name));

				// Load permissions, data, and worlds
				loadPermissions(group);
				loadData(group);
				loadWorlds(group);
				
				// Turn auto-save back on and add the group
				group.setAutoSave(true);
				groups.add(group);
			}

			// Load inheritance - All groups must be loaded first
			for (Group group : groups) {
				loadInheritance(group);
			}

			logger.info("Group data loaded. " + groups.size() + " unique groups loaded!");

		} catch (SQLException e) {
			logger.severe("Failed to load group data: " + e.getMessage());
		}
	}
	
	private void loadInheritance(Group group) {
	}
	
	private void loadPermissions(Group group) {
	}
	
	private void loadWorlds(Group group) {
	}
	
	private void loadData(Group group) {
	}

	@Override
	public void addGroup(String name) {
		try {
			groups.add(new Group(name));
			groupTable.add(new String[] {"name"}, new String[] {name});
		} catch (SQLException e) {
			logger.severe("Failed to add group " + name + ": " + e.getMessage());
		}
	}

	@Override
	public void removeGroup(String name) {
		try {
			groupTable.remove("name", name);
			for (Group group : groups) {
				if (group.getName().equalsIgnoreCase(name)) {
					groups.remove(group);
				}
			}
			
		} catch (SQLException e) {
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

	@Override
	public void saveGroup(Group group) {
	}
	
	private void saveInheritance(Group group) {
	}
	
	private void savePermissions(Group group) {
	}
	
	private void saveData(Group group) {
	}
}
