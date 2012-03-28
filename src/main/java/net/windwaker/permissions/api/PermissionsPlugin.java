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
 * Represents a plugin to populate the Permissions platform.
 *
 * @author Windwaker
 */
public interface PermissionsPlugin {

	/**
	 * Gets the plugin's GroupManager
	 *
	 * @return the GroupManager
	 */
	public GroupManager getGroupManager();

	/**
	 * Gets the plugin's UserManager.
	 *
	 * @return the UserManager
	 */
	public UserManager getUserManager();

}
