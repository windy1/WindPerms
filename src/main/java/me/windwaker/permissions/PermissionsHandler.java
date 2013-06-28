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
package me.windwaker.permissions;

import java.util.logging.Logger;

import me.windwaker.permissions.io.GroupManager;
import me.windwaker.permissions.io.UserManager;
import me.windwaker.permissions.permissible.Group;
import me.windwaker.permissions.permissible.Permissible;
import me.windwaker.permissions.permissible.User;
import me.windwaker.permissions.io.Settings;

import org.spout.api.data.DataValue;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.event.server.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGroupsEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;

import static org.spout.api.Spout.*;

/**
 * Handles all calls in SpoutAPI like PermissionsSubject.getGroups(), PermissionsSubject.isInGroup(String group),
 * PermissionsSubject.hasPermission(String node), or DataSubject.getData(String node).
 * @author Windwaker
 */
public class PermissionsHandler implements Listener {
	private final WindPerms plugin;
	private final UserManager userManager;
	private final GroupManager groupManager;

	public PermissionsHandler(WindPerms plugin) {
		this.plugin = plugin;
		userManager = plugin.getUserManager();
		groupManager = plugin.getGroupManager();
	}

	/**
	 * Catches the PermissionsGroupsEvent and sets the result to the subjects group.
	 * This is invoked when PermissionsSubject.getGroups() is called.
	 * @param event of invocation
	 */
	@EventHandler(order = Order.EARLIEST)
	public void getGroups(PermissionGroupsEvent event) {
		// Get the user
		String subject = event.getSubject().getName();
		debug("Groups requested for user: " + subject);
		User user = userManager.getUser(subject);
		if (user == null) {
			debug("\tUser not found.");
			return;
		}

		// Get the users group
		Group group = user.getGroup();
		if (group == null)
			throw new IllegalStateException("Specified user exists but does not have a group.");

		// Return the group
		String groupName = group.getName();
		debug("\tReturning group: " + groupName);
		String[] name = {groupName};
		event.setGroups(name);
	}

	/**
	 * Catches the PermissionNodeEvent and sets the result to whether or not the subject has the permission node.
	 * This is invoked when PermissionsSubject.hasPermission(String node) is called.
	 * @param event of invocation
	 */
	@EventHandler(order = Order.EARLIEST)
	public void getNodes(PermissionNodeEvent event) {
		// Get the subject - hasPermission(String node) can be called on a group or a user
		String name = event.getSubject().getName();
		debug("Permission nodes requested for subject: " + name);
		Permissible subject = groupManager.getGroup(name) != null ? groupManager.getGroup(name) : userManager.getUser(name);
		if (subject == null) {
			debug("\tSubject not found.");
			return;
		}

		/*
		 * Get the node and all parent nodes of the events.
		 *	For instance, if 'foo.bar.baz' is queried, 
		 *	PermissionNodeEvent.getNodes() will return 'foo.bar.baz', 'foo.bar.*', and 'foo.*'.
		 * 
		 * Then loop through the nodes to see if the subject has any of the nodes and set the result.
		 * If one node is found, return. We only need one node to grant permission.
		 */
		for (String node : event.getNodes()) {
			if (subject.hasPermission(node) || (subject.hasPermission("*") && Settings.WILDCARD_ENABLED.getBoolean())) {
				debug("\tSubject has permission.");
				event.setResult(Result.ALLOW);
				return;
			} else {
				event.setResult(Result.DEFAULT);
			}
		}
		debug("\tSubject does not have permission.");
	}

	/**
	 * Catches the RetrieveDataEvent and sets the result to the meta-data that was queried.
	 * This is invoked when DataSubject.getData(String node) is called.
	 * @param event of invocation
	 */
	@EventHandler(order = Order.EARLIEST)
	public void retrieveData(RetrieveDataEvent event) {
		// Gets the subject (group or user)
		String name = event.getSubject().getName();
		debug("Data requested for subject: " + name);
		Permissible subject = groupManager.getGroup(name) != null ? groupManager.getGroup(name) : userManager.getUser(name);
		if (subject == null) {
			debug("\tSubject not found.");
			return;
		}

		// Set the data if we have some
		String node = event.getNode();
		debug("\tKey: " + node);
		if (subject.hasMetadata(node)) {
			DataValue value = subject.getMetadata(node);
			debug("\tValue: " + value);
			event.setResult(value);
		}
		debug("\tData not found.");
	}

	/**
	 * Catches the PlayerLoginEvent and creates a user profile if non-existent.
	 * This is invoked when a player joins.
	 * @param event
	 */
	@EventHandler(order = Order.EARLIEST)
	public void playerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getName();
		User user = userManager.getUser(playerName);
		Logger logger = plugin.getLogger();
		if (user != null) {
			logger.info(playerName + " returned, found Permissions profile.");
			return;
		}
		logger.info(playerName + " does not have a Permissions profile, creating...");
		userManager.addUser(playerName);
	}
}
