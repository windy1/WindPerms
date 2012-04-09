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
	private final Table permissionsTable = new Table(connection, "permissions_group_permissions");
	private final PermissionsLogger logger = Permissions.getLogger();
	private final Set<Group> groups = new HashSet<Group>();
	
	@Override
	public void load() {
		try {
			
			// Create the group table
			logger.info("Loading group data...");
			if (!groupTable.exists()) {
				System.out.println("Creating table...");
				Map<String, DataType> columnDataTypeMap = new HashMap<String, DataType>();
				columnDataTypeMap.put("name", new DataType(DataType.TEXT));
				columnDataTypeMap.put("def", new DataType(DataType.CHARACTER, "1"));
				columnDataTypeMap.put("per_world", new DataType(DataType.CHARACTER, "1"));
				groupTable.create(columnDataTypeMap);
				System.out.println("Table created!");
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
			}

		} catch (SQLException e) {
			logger.severe("Failed to load group data: " + e.getMessage());
		}
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
