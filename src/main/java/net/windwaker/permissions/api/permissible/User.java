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

import java.util.Map;
import java.util.Set;

import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import org.spout.api.data.DataValue;

/**
 * Represents a user entity.
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
		
		// Inherit metadata
		Set<Map.Entry<String, DataValue>> data = group.getMetadataMap().entrySet();
		for (Map.Entry<String, DataValue> d : data) {
			if (!metadata.containsKey(d.getKey())) {
				metadata.put(d.getKey(), d.getValue());
			}
		}

		if (autoSave) {
			save();
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

	@Override
	public String toString() {
		String groupName = group != null ? group.getName() : "None";
		return "PermissionsUser{name=" + name + ",group=" + groupName + "}";
	}
}
