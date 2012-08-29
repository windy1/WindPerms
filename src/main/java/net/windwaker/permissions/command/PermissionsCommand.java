/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package net.windwaker.permissions.command;

import net.windwaker.permissions.command.sub.GroupCommand;
import net.windwaker.permissions.command.sub.HelpCommand;
import net.windwaker.permissions.command.sub.UserCommand;
import net.windwaker.permissions.command.sub.VersionCommand;

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.command.annotated.NestedCommand;
import org.spout.api.exception.CommandException;

/**
 * Handles all commands starting with 'permissions'
 * @author Windwaker
 */
public class PermissionsCommand {
	@Command(aliases = {"permissions", "pr"}, desc = "General permissions command.")
	@CommandPermissions("permissions.command.permissions")
	@NestedCommand(value = {GroupCommand.class, UserCommand.class, HelpCommand.class, VersionCommand.class})
	public void permissions(CommandContext args, CommandSource source) throws CommandException {
	}
}
