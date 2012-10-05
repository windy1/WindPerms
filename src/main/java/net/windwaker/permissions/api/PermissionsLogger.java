/* Copyright (c) 2012 Walker Crouse, <http://windwaker.net/>
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

import java.util.logging.Level;

import org.spout.api.Spout;

/**
 * A logger specific to the Permissions platform.
 * @author Windwaker
 */
public class PermissionsLogger {
	private static PermissionsLogger instance = new PermissionsLogger();

	private PermissionsLogger() {
	}

	/**
	 * Returns the singleton instance of the logger
	 *
	 * @return instance of this logger
	 */
	public static PermissionsLogger getInstance() {
		return instance;
	}

	/**
	 * Logs a message to the console with given level.
	 * @param level
	 * @param obj
	 */
	public void log(Level level, Object obj) {
		Spout.getLogger().log(level, "[WindPerms] " + obj.toString());
	}

	/**
	 * Logs a message to the console with "INFO" level.
	 * @param obj
	 */
	public void info(Object obj) {
		log(Level.INFO, obj);
	}

	/**
	 * Logs a message to the console with "WARNING" level.
	 * @param obj
	 */
	public void warning(Object obj) {
		log(Level.WARNING, obj);
	}

	/**
	 * Logs a message to the console with "SEVERE" level.
	 * @param obj
	 */
	public void severe(Object obj) {
		log(Level.SEVERE, obj);
	}
}
