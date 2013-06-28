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
package me.windwaker.permissions.permissible;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.data.DataValue;

/**
 * Represents a permissible entity.
 * @author Windwaker
 */
public abstract class Permissible {
	protected final String name;
	protected final Map<String, Boolean> permissionNodes = new HashMap<String, Boolean>();
	protected final Map<String, Boolean> inheritedNodes = new HashMap<String, Boolean>();
	protected final Map<String, DataValue> metadata = new HashMap<String, DataValue>();
	protected final Map<String, DataValue> inheritedMetadata = new HashMap<String, DataValue>();
	protected boolean autoSave = true;

	public Permissible(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of this Permissible
	 * @return name of subject
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the inherited nodes of the subject.
	 * @return inherited nodes
	 */
	public Map<String, Boolean> getInheritedPermissions() {
		return inheritedNodes;
	}

	/**
	 * Gets all permissions of subject.
	 * @return all permissions
	 */
	public Map<String, Boolean> getPermissions() {
		return permissionNodes;
	}

	/**
	 * Sets a permissions state for the subject
	 * @param node to set
	 * @param state of node
	 */
	public void setPermission(String node, boolean state) {
		permissionNodes.put(node, state);
		if (autoSave) save();
	}

	/**
	 * Whether or not the subject has permissions for said node.
	 * @param node to check
	 * @return true if permission
	 */
	public boolean hasPermission(String node) {
		if (permissionNodes.containsKey(node)) {
			return permissionNodes.get(node);
		} else if (inheritedNodes.containsKey(node)) {
			return inheritedNodes.get(node);
		}
		return false;
	}

	/**
	 * Returns inherited data mapping.
	 * @return data
	 */
	public Map<String, DataValue> getInheritedMetadataMap() {
		return inheritedMetadata;
	}

	/**
	 * Returns data mapping of entity.
	 * @return data
	 */
	public Map<String, DataValue> getMetadataMap() {
		return metadata;
	}

	/**
	 * Adds a data entry.
	 * @param node to set
	 * @param value to set for node
	 */
	public void setMetadata(String node, DataValue value) {
		metadata.put(node, value);
		if (autoSave) save();
	}

	/**
	 * Adds a data entry
	 * @param node to set
	 * @param value to set for node
	 */
	public void setMetadata(String node, Object value) {
		setMetadata(node, new DataValue(value));
	}

	/**
	 * Gets an entry from given node.
	 * @param node to set
	 * @return data value.
	 */
	public DataValue getMetadata(String node) {
		if (metadata.containsKey(node)) {
			return metadata.get(node);
		} else if (inheritedMetadata.containsKey(node)) {
			return inheritedMetadata.get(node);
		}
		return null;
	}

	/**
	 * If the entity has an data entry for given node.
	 * @param node to check
	 * @return true if has data
	 */
	public boolean hasMetadata(String node) {
		return getMetadata(node) != null;
	}

	/**
	 * Sets whether or not the entity should save to disk automatically.
	 * @param autoSave whether this should auto-save to disk when modified.
	 */
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	/**
	 * Whether or not the entity auto-saves.
	 * @return true if auto-saves.
	 */
	public boolean isAutoSave() {
		return autoSave;
	}

	/**
	 * Saves the entity to disk.
	 */
	public abstract void save();

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Permissible && ((Permissible) obj).getName().equalsIgnoreCase(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
