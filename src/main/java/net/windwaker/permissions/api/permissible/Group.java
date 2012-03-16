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
package net.windwaker.permissions.api.permissible;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import org.spout.api.data.DataValue;

/**
 * @author Windwaker
 */
public class Group implements Permissible {

	private final GroupManager groupManager = Permissions.getGroupManager();
	private final String name;
	private boolean def = false;
	private boolean canBuild = true;
	private Map<Group, Boolean> inherited = new HashMap<Group, Boolean>();
	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	private Map<String, DataValue> data = new HashMap<String, DataValue>();

	public Group(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "PermissionsGroup{name=" + name + ",default=" + def + ",canBuild=" + canBuild + "}";
	}

	/**
	 * Gets any inherited groups.
	 *
	 * @return inherited groups.
	 */
	public Map<Group, Boolean> getInheritedGroups() {
		return inherited;
	}

	/**
	 * Adds an inherited group.
	 *
	 * @param group
	 */
	public void setInheritedGroup(Group group, boolean inherit) {
		inherited.put(group, inherit);
		groupManager.saveGroup(this);
	}

	/**
	 * Sets if the group should be the default group.
	 *
	 * @param def
	 */
	public void setDefault(boolean def) {
		this.def = def;
		groupManager.saveGroup(this);
	}

	/**
	 * Whether or not the group is a default group
	 *
	 * @return true if default
	 */
	public boolean isDefault() {
		return def;
	}

	@Override
	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	@Override
	public void setPermission(String node, boolean state) {
		permissions.put(node, state);
		groupManager.saveGroup(this);
	}

	@Override
	public boolean hasPermission(String node) {
		if (permissions.containsKey(node)) {
			return permissions.get(node);
		}

		return false;
	}

	@Override
	public void setCanBuild(boolean canBuild) {
		this.canBuild = canBuild;
		groupManager.saveGroup(this);
	}

	@Override
	public boolean canBuild() {
		return canBuild;
	}

	@Override
	public Map<String, DataValue> getMetadata() {
		return data;
	}

	@Override
	public void setMetadata(String identifier, DataValue value) {
		data.put(identifier, value);
	}

	@Override
	public DataValue getMetadata(String identifier) {
		return data.get(identifier);
	}
}
