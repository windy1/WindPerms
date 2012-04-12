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
package net.windwaker.permissions;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.PermissionsLogger;
import net.windwaker.permissions.api.Permissions;
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

		for (String node : event.getNodes()) {
			if (subject.hasPermission(node) || subject.hasPermission("*")) {
				event.setResult(Result.ALLOW);
				return;
			} else {
				event.setResult(Result.DENY);
			}
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
