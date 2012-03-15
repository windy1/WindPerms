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
package net.windwaker.permissions;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.permissible.Group;

import org.spout.api.util.config.Configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Windwaker
 */
public class SimpleGroupManager implements GroupManager {

	private static final Configuration data = new Configuration(new File("plugins/Permissions/groups.yml"));
	private final Set<Group> groups = new HashSet<Group>();
	
	public static void init() {
		data.load();
	}
	
	@Override
	public void addGroup(String name) {
		groups.add(new Group(name));
		String path = "groups." + name;
		data.setValue(path + ".inherited.admin", false);
		data.setValue(path + ".default", false);
		data.setValue(path + ".permissions.foo", false);
		data.setValue(path + ".permissions.bar", false);
		data.setValue(path + ".build", true);
		data.save();
	}

	@Override
	public void removeGroup(String name) {
		data.setValue("groups." + name, null);
		data.save();
	}

	@Override
	public Group getGroup(String name) {
		for (Group group : groups) {
			if (group.getName().equals(name)) {
				return group;
			}
		}

		return null;
	}
	
	@Override
	public Set<Group> getGroups() {
		return groups;
	}
}
