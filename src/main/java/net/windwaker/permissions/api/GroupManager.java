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
package net.windwaker.permissions.api;

import net.windwaker.permissions.api.permissible.Group;

import java.util.Set;

/**
 *
 * @author Windwaker
 */
public interface GroupManager {

	/**
	 * Adds a group
	 *
	 * @param name
	 */
	public void addGroup(String name);

	/**
	 * Removes a group
	 *
	 * @param name
	 */
	public void removeGroup(String name);

	/**
	 * Gets a group from said name
	 *
	 * @param name
	 * @return group
	 */
	public Group getGroup(String name);

	/**
	 * Gets all defined groups.
	 *
	 * @return all groups.
	 */
	public Set<Group> getGroups();

	/**
	 * Saves a group from said name
	 *
	 * @param group
	 */
	public void saveGroup(Group group);

}
