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

import org.spout.api.Spout;

import java.util.logging.Level;

/**
 * A logger specific to the Permissions plugin.
 *
 * @author Windwaker
 */
public class Logger {
	
	private static final Logger instance = new Logger();
	
	private Logger() {
		
	}
	
	public static Logger getInstance() {
		return instance;
	}
	
	public void log(Level level, Object obj) {
		Spout.getLogger().log(level, "[Permissions] " + obj.toString());
	}
	
	public void info(Object obj) {
		log(Level.INFO, obj);
	}
	
	public void warning(Object obj) {
		log(Level.WARNING, obj);
	}
	
	public void severe(Object obj) {
		log(Level.SEVERE, obj);
	}
}
