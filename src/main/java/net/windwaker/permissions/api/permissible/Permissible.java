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
 * Represents a permissible entity.
 *
 * @author Windwaker
 */
public abstract class Permissible {
	protected final String name;
	protected final Map<String, Boolean> permissionNodes = new HashMap<String, Boolean>();
	protected boolean canBuild = true;
	protected boolean autoSave = true;

	public Permissible(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of this Permissible
	 *
	 * @return name of subject
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets all permissions of subject, including any inherited.
	 *
	 * @return all permissions
	 */
	public Map<String, Boolean> getPermissions() {
		return permissionNodes;
	} 

	/**
	 * Sets a permissions state for the subject
	 *
	 * @param node
	 * @param state
	 */
	public void setPermission(String node, boolean state) {
		permissionNodes.put(node, state);
		if (autoSave) {
			save();
		}
	}

	/**
	 * Whether or not the subject has permissions for said node.
	 *
	 * @param node
	 * @return true if permission
	 */
	public boolean hasPermission(String node) {
		if (permissionNodes.containsKey(node)) {
			return permissionNodes.get(node);
		}

		return false;
	}

	/**
	 * Sets if the subject can build
	 *
	 * @param canBuild
	 */
	public void setCanBuild(boolean canBuild) {
		this.canBuild = canBuild;
		if (autoSave) {
			save();
		}
	}

	/**
	 * Whether or not the subject can build.
	 *
	 * @return true if subject can build.
	 */
	public boolean canBuild() {
		return canBuild;
	}

	/**
	 * Sets whether or not the entity should save to disk automatically.
	 *
	 * @param autoSave
	 */
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	/**
	 * Whether or not the entity auto-saves.
	 *
	 * @param autoSave
	 * @return true if auto-saves.
	 */
	public boolean isAutoSave(boolean autoSave) {
		return autoSave;
	}

	/**
	 * Saves the entity to disk.
	 */
	public abstract void save();
}
