/*
 * Copyright (c) 2012-2013 ${developer}, <http://windwaker.me>
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
package me.windwaker.permissions.api.permissible;

import java.util.Map;

import me.windwaker.permissions.WindPerms;
import me.windwaker.permissions.api.UserManager;

import org.spout.api.data.DataValue;

/**
 * Represents a user entity.
 * @author Windwaker
 */
public class User extends Permissible {
	private final UserManager userManager;
	private Group group;

	/**
	 * Constructs a new user with the specified name.
	 * @param name
	 */
	public User(WindPerms plugin, String name) {
		super(name);
		userManager = plugin.getUserManager();
	}

	/**
	 * Sets the user's group
	 * @param group
	 */
	public void setGroup(Group group) {

		// Update group references to user
		if (this.group != null) {
			this.group.removeUser(this);
		}
		this.group = group;
		group.addUser(this);

		// inherit the group's data
		inherit(group);

		// Save
		if (autoSave) {
			save();
		}
	}

	public void inherit(Group group) {
		// inherit nodes
		for (Map.Entry<String, Boolean> node : group.getInheritedPermissions().entrySet()) {
			inheritedNodes.put(node.getKey(), node.getValue());
		}

		for (Map.Entry<String, Boolean> node : group.getPermissions().entrySet()) {
			inheritedNodes.put(node.getKey(), node.getValue());
		}

		// inherit data
		for (Map.Entry<String, DataValue> data : group.getInheritedMetadataMap().entrySet()) {
			inheritedMetadata.put(data.getKey(), data.getValue());
		}

		for (Map.Entry<String, DataValue> data : group.getMetadataMap().entrySet()) {
			inheritedMetadata.put(data.getKey(), data.getValue());
		}
	}

	/**
	 * Gets the user's group.
	 * @return the user's group.
	 */
	public Group getGroup() {
		return group;
	}

	@Override
	public void save() {
		userManager.saveUser(this);
	}
}
