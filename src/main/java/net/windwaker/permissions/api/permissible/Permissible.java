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
import org.spout.api.data.DataValue;

/**
 * @author Windwaker
 */
public interface Permissible {

	/**
	 * Gets the name of this Permissable
	 *
	 * @return name of subject
	 */
	public String getName();

	/**
	 * Gets all permissions of subject, including any inherited.
	 *
	 * @return all permissions
	 */
	public Map<String, Boolean> getPermissions();

	/**
	 * Sets a permissions state for the subject
	 *
	 * @param node
	 * @param state
	 */
	public void setPermission(String node, boolean state);

	/**
	 * Whether or not the subject has permissions for said node.
	 *
	 * @param node
	 * @return true if permission
	 */
	public boolean hasPermission(String node);

	/**
	 * Sets if the subject can build
	 *
	 * @param canBuild
	 */
	public void setCanBuild(boolean canBuild);

	/**
	 * Whether or not the subject can build.
	 *
	 * @return true if subject can build.
	 */
	public boolean canBuild();
	
	/**
	 * Sets a metadata value to the subject.
	 * 
	 * @param unique identifier 
	 * @param value
	 */
	public void setMetadata(String identifier, DataValue value);
	
	/**
	 * Gets a metadata value from the subject via a unique identifier.
	 * 
	 * @param identifier
	 * @return value
	 */
	public DataValue getMetadata(String identifier);

}
