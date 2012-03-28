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

/**
 * Represents the Permissions platform.
 *
 * @author Windwaker
 */
public class Permissions {
	private static PermissionsPlugin instance = null;

	private Permissions() {

	}

	/**
	 * Sets the PermissionsPlugin of the Permissions platform.
	 * 
	 * @param plugin 
	 */
	public static void setPlugin(PermissionsPlugin plugin) {
		if (instance == null) {
			instance = plugin;
		}
	}

	/**
	 * Gets the GroupManager associated with Permissions.
	 * 
	 * @return GroupManager
	 */
	public static GroupManager getGroupManager() {
		return instance.getGroupManager();
	}

	/**
	 * Gets the UserManager associated with Permissions.
	 * 
	 * @return UserManager
	 */
	public static UserManager getUserManager() {
		return instance.getUserManager();
	}
}
