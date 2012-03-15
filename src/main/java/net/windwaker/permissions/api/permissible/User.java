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

/**
 * @author Windwaker
 */
public class User implements Permissible {

	private final String name;
	private Group group;
	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	private boolean canBuild = false;
	
	public User(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the user
	 *
	 * @return username
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the user's group
	 *
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
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
	}

	@Override
	public boolean hasPermission(String node) {
		return permissions.get(node);
	}

	@Override
	public void setCanBuild(boolean canBuild) {
		this.canBuild = canBuild;
	}

	@Override
	public boolean canBuild() {
		return canBuild;
	}
}
