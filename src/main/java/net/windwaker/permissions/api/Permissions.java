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
package net.windwaker.permissions.api;

/**
 * Represents the Permissions platform.
 * @author Windwaker
 */
public class Permissions {
	private static PermissionsPlugin instance = null;
	private static final PermissionsLogger logger = new PermissionsLogger();

	private Permissions() {
	}

	/**
	 * Sets the PermissionsPlugin of the Permissions platform.
	 * @param plugin
	 */
	public static void setPlugin(PermissionsPlugin plugin) {
		instance = plugin;
	}

	/**
	 * Gets the running plugin.
	 * @return plugin
	 */
	public static PermissionsPlugin getPlugin() {
		return instance;
	}

	/**
	 * Gets the logger specific to Permissions.
	 * @return logger.
	 */
	public static PermissionsLogger getLogger() {
		return logger;
	}

	/**
	 * Gets the GroupManager associated with Permissions.
	 * @return GroupManager
	 */
	public static GroupManager getGroupManager() {
		return instance.getGroupManager();
	}

	/**
	 * Gets the UserManager associated with Permissions.
	 * @return UserManager
	 */
	public static UserManager getUserManager() {
		return instance.getUserManager();
	}
}
