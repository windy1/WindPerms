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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import me.windwaker.permissions.api.GroupManager;
import me.windwaker.permissions.api.UserManager;
import me.windwaker.permissions.cmd.sub.GroupCommands;
import me.windwaker.permissions.cmd.sub.PermissionsCommands;
import me.windwaker.permissions.cmd.sub.UserCommands;
import me.windwaker.permissions.io.Settings;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandManager;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.entity.Player;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginDescriptionFile;

/**
 * Implementation of PermissionsPlugin
 * @author Windwaker
 */
public class WindPerms extends Plugin {
	private PermissionsHandler handler;
	private Settings settings;
	private GroupManager groupManager;
	private UserManager userManager;

	/**
	 * Loads all data within the plugin.
	 */
	private void load() {
		loadData();
		loadPlayers();
		handler = new PermissionsHandler(this);
	}

	private void loadData() {
		// Create and load data
		settings = new Settings(this);
		settings.load();
		// Create and load group manager
		groupManager = settings.createGroupManager();
		groupManager.load();
		// Create and load user manager
		userManager = settings.createUserManager();
		userManager.load();
	}

	private void loadPlayers() {
		Engine engine = getEngine();
		if (engine instanceof Server) {
			userManager.clear();
			for (Player player : ((Server) engine).getOnlinePlayers()) {
				userManager.addUser(player.getName());
			}
		}
	}

	private void registerCommands() {
		CommandManager cm = Spout.getCommandManager();
		// register group commands
		Command group = cm.getCommand("group").addAlias("g");
		AnnotatedCommandExecutorFactory.create(new GroupCommands(this), group);

		// register main commands
		Command permissions = cm.getCommand("permissions").addAlias("pr", "windperms", "wp");
		AnnotatedCommandExecutorFactory.create(new PermissionsCommands(this), permissions);

		// register user commands
		Command user = cm.getCommand("user").addAlias("u");
		AnnotatedCommandExecutorFactory.create(new UserCommands(this), user);
	}

	/**
	 * Saves all data in the plugin.
	 */
	private void save() {
		// Save all data
		settings.save();
		groupManager.save();
		userManager.save();
	}

	/**
	 * Gets the {@link GroupManager} of WindPerms
	 *
	 * @return group manager
	 */
	public GroupManager getGroupManager() {
		return groupManager;
	}

	/**
	 * Gets the {@link UserManager} of WindPerms
	 *
	 * @return user manager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	@Override
	public void onReload() {
		// Load data directly from disk
		load();
		getLogger().info("WindPerms " + getDescription().getVersion() + " reloaded.");
	}

	@Override
	public void onLoad() {
		// Load data
		load();
	}

	@Override
	public void onEnable() {
		// Register events
		getEngine().getEventManager().registerEvents(handler, this);
		registerCommands();
		getLogger().info("WindPerms " + getDescription().getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		// Save data
		save();
		getLogger().info("WindPerms " + getDescription().getVersion() + " disabled.");
	}

	@Override
	public URI getUpdate() {
		// create the URL
		PluginDescriptionFile pdf = getDescription();
		String repoUrl = pdf.getData("repo-url");
		String repoId = pdf.getData("repo-id");
		String groupId = pdf.getData("group-id");
		String artifactId = pdf.getData("artifact-id");
		String updateVersion = pdf.getData("update-version");
		String path = repoUrl + "/service/local/artifact/maven/"
				+ "redirect?r=" + repoId
				+ "&g=" + groupId
				+ "&a=" + artifactId
				+ "&v=" + updateVersion;

		System.out.println("URL: "  + path);

		// check if there is a newer version
		try {
			URI uri = new URI(path);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setInstanceFollowRedirects(false);

			// make sure the url should redirect
			int response = connection.getResponseCode();
			System.out.println("Response: " + response);
			if (response != 301) return null;

			// get the version from the suggested filename
			String v = connection.getHeaderField("Location");
			System.out.println("Location: " + v);
			v = v.substring(v.lastIndexOf('/')).split("-")[1];
			System.out.println("Version: " + v);
			String curr = pdf.getVersion().split("-")[0];
			System.out.println("Current version: " + curr);

			// compare the suggested version with the current version
			DefaultArtifactVersion newest = new DefaultArtifactVersion(v);
			DefaultArtifactVersion current = new DefaultArtifactVersion(curr);
			boolean available = newest.compareTo(current) > 0;
			System.out.println("Newer version available: " + available);
			return available ? uri : null;
		} catch (URISyntaxException e) {
			throw new SpoutRuntimeException("Error creating update URI.", e);
		} catch (MalformedURLException e) {
			throw new SpoutRuntimeException("Error opening connection to check for update.", e);
		} catch (IOException e) {
			throw new SpoutRuntimeException("Error opening update connection", e);
		}
	}
}
