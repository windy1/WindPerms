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
import java.util.Map;

import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;


/**
 * @author Windwaker
 */
public class User implements Permissible {

	private final UserManager userManager = Permissions.getUserManager();
	private final String name;
	private Group group;
	private final Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	//private Map<String, DataValue> data = new HashMap<String, DataValue>();
	private boolean canBuild = false;
	
	public User(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		String groupName = group != null ? group.getName() : "None";
		return "PermissionsUser{name=" + name + ",group=" + groupName + ",canBuild=" + canBuild + "}";
	}

	/**
	 * Sets the user's group
	 *
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
		userManager.saveUser(this);
	}

	/**
	 * Gets the user's group.
	 *
	 * @return the user's group.
	 */
	public Group getGroup() {
		return group;
	}

	@Override
	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	@Override
	public void setPermission(String node, boolean state) {
		permissions.put(node, state);
		userManager.saveUser(this);
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
		userManager.saveUser(this);
	}

	@Override
	public boolean canBuild() {
		return canBuild;
	}

	/*
	@Override
	public Map<String, DataValue> getMetadata() {
		return data;
	}

	@Override
	public void setMetadata(String identifier, DataValue value) {
		data.put(identifier, value);
		userManager.saveUser(this);
	}

	@Override
	public DataValue getMetadata(String identifier) {
		return data.get(identifier);
	}*/
}
