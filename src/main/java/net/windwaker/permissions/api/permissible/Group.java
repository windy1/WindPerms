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

import java.util.Map;
import java.util.Set;

/**
 * @author Windwaker
 */
public class Group implements Permissible {

	private final String name;
	private Set<Group> inherited;
	private boolean def = false;
	private boolean canBuild = true;
	private Map<String, Boolean> permissions;

	public Group(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of this group.
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Gets any inherited groups.
	 *
	 * @return inherited groups.
	 */
	public Set<Group> getInheritedGroups() {
		return inherited;
	}

	/**
	 * Adds an inherited group.
	 *
	 * @param group
	 */
	public void addInheritedGroup(Group group) {
		inherited.add(group);
	}

	/**
	 * Sets if the group should be the default group.
	 *
	 * @param def
	 */
	public void setDefault(boolean def) {
		this.def = def;
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
