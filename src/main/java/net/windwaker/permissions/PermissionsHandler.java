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
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Group;
import net.windwaker.permissions.api.permissible.Permissible;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.event.server.data.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;

public class PermissionsHandler implements Listener {
	private final UserManager userManager = Permissions.getUserManager();
	private final GroupManager groupManager = Permissions.getGroupManager();
	private final PermissionsLogger logger = Permissions.getLogger();
	
	@EventHandler(order = Order.EARLIEST)
	public void onGroupsGet(PermissionGetGroupsEvent event) {
		User user = userManager.getUser(event.getSubject().getName());
		if (user == null) {
			return;
		}
		
		Group group = user.getGroup();
		if (group == null) {
			return;
		}
		
		String[] name = {group.getName()};
		event.setGroups(name);
	}

	@EventHandler(order = Order.EARLIEST)
	public void onGroupCheck(PermissionGroupEvent event) {
		User user = userManager.getUser(event.getSubject().getName());
		if (user == null) {
			return;
		}
		
		Group group = user.getGroup();
		if (group == null) {
			return;
		}

		String name = event.getGroup();
		if (group.getName().equalsIgnoreCase(name)) {
			event.setResult(true);
		} else {
			event.setResult(false);
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onNodeCheck(PermissionNodeEvent event) {
		String name = event.getSubject().getName();
		Permissible subject = groupManager.getGroup(name) != null ? groupManager.getGroup(name) : userManager.getUser(name);
		if (subject == null) {
			return;
		}
		
		if (subject.hasPermission(event.getNode()) || subject.hasPermission("*")) {
			event.setResult(Result.ALLOW);
		} else {
			event.setResult(Result.DENY);
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onDataGet(RetrieveDataEvent event) {
		String name = event.getSubject().getName();
		Permissible subject = groupManager.getGroup(name) != null ? groupManager.getGroup(name) : userManager.getUser(name);
		if (subject == null) {
			return;
		}

		String node = event.getNode();
		if (subject.hasMetadata(node)) {
			event.setResult(subject.getMetadata(node));
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getName();
		User user = userManager.getUser(playerName);
		if (user != null) {
			logger.info(playerName + " returned, found Permissions profile.");
			return;
		}

		logger.info(playerName + " does not have a Permissions profile, creating...");
		userManager.addUser(playerName);

	}
}
