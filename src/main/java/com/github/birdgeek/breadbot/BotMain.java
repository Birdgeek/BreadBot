package com.github.birdgeek.breadbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.StatsFile;

public class BotMain {
	
	public static long start;
	static ConfigFile config;
	static StatsFile stats;
	public static Logger discordLog;
	public static Logger ircLog;
	public static Logger systemLog;
	public final static String version = "0.0.7";
	
	/*
	 * Main method  for Breadbot
	 */
	public static void main(String[] args)  {
		
		discordLog = LoggerFactory.getLogger("Discord");
		ircLog = LoggerFactory.getLogger("IRC");
		systemLog = LoggerFactory.getLogger("System");
	
		
		config = new ConfigFile(systemLog);
		stats = new StatsFile();
		
		start = System.currentTimeMillis();
		

		discordLog.debug("Logging in using: " + ConfigFile.getBotToken());
		DiscordMain.setup(discordLog);
		
		
		if (ConfigFile.shouldEnableIrc()) { //Should we enable the IRC portion?
			IrcMain.setup(ircLog);
			systemLog.info("Enabled twitch");
		}
	}
}
