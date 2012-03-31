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

import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;

/**
 * Represents a user entity.
 *
 * @author Windwaker
 */
public class User extends Permissible {
	private final UserManager userManager = Permissions.getUserManager();
	private Group group;

	public User(String name) {
		super(name);
	}

	/**
	 * Sets the user's group
	 *
	 * @param group
	 */
	public void setGroup(Group group) {
		// Set the group and inherit permission nodes.
		this.group = group;
		Set<Map.Entry<String, Boolean>> nodes = group.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> node : nodes) {
			if (!permissionNodes.containsKey(node.getKey())) {
				permissionNodes.put(node.getKey(), node.getValue());
			}
		}

		if (autoSave) {
			save();
		}
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
	public void save() {
		userManager.saveUser(this);
	}

	@Override
	public String toString() {
		String groupName = group != null ? group.getName() : "None";
		return "PermissionsUser{name=" + name + ",group=" + groupName + "}";
	}
}
