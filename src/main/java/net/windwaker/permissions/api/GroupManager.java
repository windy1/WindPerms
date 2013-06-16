/* Copyright (c) 2012 Walker Crouse, <http://windwaker.net/>
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
package net.windwaker.permissions.api;

import java.util.Set;

import net.windwaker.permissions.api.permissible.Group;

/**
 * Utility class for group management.
 * @author Windwaker
 */
public interface GroupManager {
	/**
	 * Loads all group data
	 */
	public void load();

	/**
	 * Saves all group data
	 */
	public void save();

	/**
	 * Adds a group
	 * @param name
	 */
	public void addGroup(String name);

	/**
	 * Removes a group
	 * @param name
	 */
	public void removeGroup(String name);

	/**
	 * Saves a group from said name
	 * @param group
	 */
	public void saveGroup(Group group);

	/**
	 * Loads a group from disk.
	 * @param group
	 */
	public void loadGroup(String group);

	/**
	 * Gets the default group.
	 * @return
	 */
	public Group getDefaultGroup();

	/**
	 * Gets a group from said name
	 * @param name
	 * @return group
	 */
	public Group getGroup(String name);

	/**
	 * Gets all defined groups.
	 * @return all groups.
	 */
	public Set<Group> getGroups();

	/**
	 * Reloads the inheritance ladder
	 */
	public void reloadInheritance();

	/**
	 * Clears all groups.
	 */
	public void clear();
}
