/* Copyright (c) 2012 Walker Crouse, http://windwaker.net/
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
package net.windwaker.permissions.api.permissible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.exception.HierarchicalException;

import org.spout.api.geo.World;

/**
 * Represents a group of users.
 * @author Windwaker
 */
public class Group extends Permissible {
	private final GroupManager groupManager = Permissions.getGroupManager();
	private boolean def = false;
	private boolean universal = true;
	private final Map<Group, Boolean> inherited = new HashMap<Group, Boolean>();
	private final List<World> worlds = new ArrayList<World>();

	public Group(String name) {
		super(name);
	}

	/**
	 * Gets any inherited groups.
	 * @return inherited groups.
	 */
	public Map<Group, Boolean> getInheritedGroups() {
		return inherited;
	}

	/**
	 * Adds an inherited group.
	 * @param group to inherit
	 */
	public void setInheritedGroup(Group group, boolean inherit) {
		if (group.isAssignableFrom(this)) {
			throw new HierarchicalException("Group " + group.getName() + " already inherits " + name + ". Two groups may not inherit each other.");
		}
		inherited.put(group, inherit);

		Set<Map.Entry<String, Boolean>> nodes = group.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> node : nodes) {
			if (!permissionNodes.containsKey(node.getKey()) && inherit) {
				permissionNodes.put(node.getKey(), node.getValue());
			}
		}

		if (autoSave) {
			save();
		}
	}

	/**
	 * Whether or not this group inherits the given group.
	 * @param group to check
	 * @return true if inherits
	 */
	public boolean isAssignableFrom(Group group) {
		if (inherited.containsKey(group)) {
			return inherited.get(group);
		}
		return false;
	}

	/**
	 * Sets if the group should be the default group.
	 * @param def
	 */
	public void setDefault(boolean def) {
		this.def = def;
		if (autoSave) {
			save();
		}
	}

	/**
	 * Whether or not the group is a default group
	 * @return true if default
	 */
	public boolean isDefault() {
		return def;
	}

	/**
	 * Whether or not the group is universal.
	 * @return true if universal
	 */
	public boolean isUniversal() {
		return universal;
	}

	/**
	 * Sets if the group is universal.
	 * @param universal
	 */
	public void setUniversal(boolean universal) {
		this.universal = universal;
		if (autoSave) {
			save();
		}
	}

	/**
	 * Gets the worlds associated with the group, does nothing if universal is true.
	 * @return
	 */
	public List<World> getWorlds() {
		return worlds;
	}

	/**
	 * Adds a world to the groups worlds.
	 * @param world
	 */
	public void addWorld(World world) {
		worlds.add(world);
		if (autoSave) {
			save();
		}
	}

	/**
	 * Removes a world from the groups world.
	 * @param world
	 */
	public void removeWorld(World world) {
		worlds.remove(world);
		if (autoSave) {
			save();
		}
	}

	@Override
	public void save() {
		groupManager.saveGroup(this);
	}

	@Override
	public String toString() {
		return "PermissionsGroup{name=" + name + ",default=" + def + "}";
	}
}
