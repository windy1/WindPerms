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

import java.util.*;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import org.spout.api.geo.World;

/**
 * @author Windwaker
 */
public class Group implements Permissible {

	private final GroupManager groupManager = Permissions.getGroupManager();
	private final String name;
	private boolean def = false;
	private boolean canBuild = true;
	private boolean perWorld = false;
	private Map<Group, Boolean> inherited = new HashMap<Group, Boolean>();
	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	private List<World> worlds = new ArrayList<World>();
	//private Map<String, DataValue> data = new HashMap<String, DataValue>();

	public Group(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "PermissionsGroup{name=" + name + ",default=" + def + ",canBuild=" + canBuild + "}";
	}

	/**
	 * Gets any inherited groups.
	 *
	 * @return inherited groups.
	 */
	public Map<Group, Boolean> getInheritedGroups() {
		return inherited;
	}

	/**
	 * Adds an inherited group.
	 *
	 * @param group
	 */
	public void setInheritedGroup(Group group, boolean inherit) {
		inherited.put(group, inherit);
		Set<Map.Entry<String, Boolean>> nodes = group.getPermissions().entrySet();
		if (inherit) {
			for (Map.Entry<String, Boolean> node : nodes) {
				if (!permissions.containsKey(node)) {
					continue;
				}
			
				permissions.put(node.getKey(), node.getValue());
			}
		}

		groupManager.saveGroup(this);
	}

	/**
	 * Sets if the group should be the default group.
	 *
	 * @param def
	 */
	public void setDefault(boolean def) {
		this.def = def;
		groupManager.saveGroup(this);
	}

	/**
	 * Whether or not the group is a default group
	 *
	 * @return true if default
	 */
	public boolean isDefault() {
		return def;
	}

	/**
	 * Whether or not the group is per-world or universal.
	 *
	 * @return true if per-world
	 */
	public boolean isPerWorld() {
		return perWorld;
	}

	/**
	 * Sets if the group is universal or per-world.
	 *
	 * @param perWorld
	 */
	public void setPerWorld(boolean perWorld) {
		this.perWorld = perWorld;
		groupManager.saveGroup(this);
	}

	/**
	 * Gets the worlds associated with the group, does nothing if per-world is false.
	 *
	 * @return
	 */
	public List<World> getWorlds() {
		return worlds;
	}

	/**
	 * Adds a world to the groups worlds.
	 *
	 * @param world
	 */
	public void addWorld(World world) {
		worlds.add(world);
		groupManager.saveGroup(this);
	}

	/**
	 * Removes a world from the groups world.
	 *
	 * @param world
	 */
	public void removeWorld(World world) {
		worlds.remove(world);
		groupManager.saveGroup(this);
	}

	@Override
	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	@Override
	public void setPermission(String node, boolean state) {
		permissions.put(node, state);
		groupManager.saveGroup(this);
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
		groupManager.saveGroup(this);
	}

	@Override
	public boolean canBuild() {
		return canBuild;
	}

	/*@Override
	public Map<String, DataValue> getMetadata() {
		return data;
	}

	@Override
	public void setMetadata(String identifier, DataValue value) {
		data.put(identifier, value);
	}

	@Override
	public DataValue getMetadata(String identifier) {
		return data.get(identifier);
	}*/
}
