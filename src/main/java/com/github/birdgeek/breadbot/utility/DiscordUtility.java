package com.github.birdgeek.breadbot.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;

import com.github.birdgeek.breadbot.BotMain;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class DiscordUtility {
	static JDA jda;
	static Logger discordLog;
	private static String helpFileName = "help.txt";
	static String[] approvedUsers = getApprovedUsers();
	
	public DiscordUtility(JDA api, Logger log) {
		DiscordUtility.jda = api;
		DiscordUtility.discordLog = log;
	}
	
	/**
	 * Returns the ID of a specific User
	 * 
	 * @param guildMessageEvent - Require this to get the Author
	 * @return Author ID
	 */
	public static String getUsernameID(GuildMessageReceivedEvent guildMessageEvent) {
		return guildMessageEvent.getAuthor().getId();
	}

	/**
	 * @return - t/f depending on if ID of user is = to ID of Owner from config
	 * 
	 */
	public static boolean isOwner(String username) {
		if (ConfigFile.getOwnerID().toString().equalsIgnoreCase(username))
			return true;
		else
			return false;
	}

	/**
	 * Deletes the command the user issued - Keeps txt channels clean
	 * @param guildMessageEvent
	 */
	public static void delMessage(GuildMessageReceivedEvent guildMessageEvent) {

		if (ConfigFile.shouldDelete()) {
			
			guildMessageEvent.getMessage().deleteMessage();
		}
	}
	/**
	 * Sends help to Text Channel
	 * @param guildMessageEvent - Required to send to text channel
	 * @return Returns the help.txt in a formatted code block
	 */
	public static void sendGlobalHelp(GuildMessageReceivedEvent guildMessageEvent) {
		guildMessageEvent.getChannel().sendMessage(new MessageBuilder()
				.appendString("Welcome to the help command! Below are all the commands you can run!")
				.appendCodeBlock(getHelpCommands(), "python")
				.build());
		
	}

	/**
	 * Sends help to Private Channel
	 * @param guildMessageEvent - Required to send to PrivateChannel from Author
	 * @return - Returns help.txt in a formatted code block <b>in the private channel</b> of the user who issued the command
	 */
	public static void sendHelp(GuildMessageReceivedEvent guildMessageEvent) {
		if (!guildMessageEvent.getAuthor().getUsername().equalsIgnoreCase(jda.getSelfInfo().getUsername())) {
			guildMessageEvent.getAuthor().getPrivateChannel().sendMessage(new MessageBuilder().appendString("Welcome to the help command! Below are all the commands you can run!")
					.appendCodeBlock(getHelpCommands(), "python").build());
		}
		else {
			guildMessageEvent.getChannel().sendMessage("Cannot send help to yourself in PM; try #global help");
		}
	}
	/**
	 * Sends help to Private channel (issued from Private Channel)
	 * @param privateChannel - Required to respond directly to Private Channel
	 * @return - Returns help.txt in a formatted code block
	 */
	public static void sendHelp(PrivateChannel privateChannel) {
		privateChannel.sendMessage(new MessageBuilder().appendCodeBlock(getHelpCommands(), "python").build());
	}
	
	/**Sends uptime to text channel where cmd was issued
	 * @param guildMessageEvent
	 * @return -Sends uptime to text channel
	 */
	public static void sendUptime(GuildMessageReceivedEvent guildMessageEvent) {
		long different = System.currentTimeMillis() - BotMain.start;
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		
		long elapsedSeconds = different / secondsInMilli;
		String time = String.format("%d days, %d hours, %d minutes, %d seconds%n", //THANKS FOR FIXING THIS SHIT VAN
		    elapsedDays,
		    elapsedHours, elapsedMinutes, elapsedSeconds);
		
		guildMessageEvent.getChannel().sendMessage( "I have been online for " + time);	
		}

	/**
	 * 
	 * @return String of everything in help.txt file
	 */
	private static String getHelpCommands() {
		
		StringBuilder sb = new StringBuilder();
		
		try {
			FileReader fr = new FileReader(helpFileName);
			
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			br.close();
			return everything;
		}
		catch (FileNotFoundException ex) {

			discordLog.info("Unable to open file: " + helpFileName);
		}
		catch (IOException ex) {
			discordLog.info("Error reading file");
		}
		return "It must have failed on me :(";
	}

	/**
	 * 
	 * @return ApprovedUsers Array
	 */
	public static String[] getApprovedUsers() {
		return ConfigFile.getApprovedUsers();
		
	}
	/**
	 * @param guildMessageEvent
	 * @return String of username
	 */
	public static String getUsername(GuildMessageReceivedEvent guildMessageEvent) {
		return guildMessageEvent.getAuthor().getUsername();
	}
	
	/**
	 * 
	 * @param username
	 * @return t/f depending on if user is approved for discord
	 */
	public static boolean isApprovedUser(String username) {
		for (int i=0; i < approvedUsers.length; i++) {
			if (username.equalsIgnoreCase(approvedUsers[i])) {
				return true;
			}
		}
			return false;
	}
	
	/**
	 * @return Prints diagnositcs to home channel
	 */
	public static void printDiagnostics() {
		
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(
				new MessageBuilder().appendCodeBlock(""
				+ "Home Channel: " + ConfigFile.getHomeChannel() + "/" + jda.getTextChannelById(ConfigFile.getHomeChannel()).getName()
				+ "\nHome Guild:" + ConfigFile.getHomeGuild() + "/" + jda.getGuildById(ConfigFile.getHomeGuild()).getName()
				+ "\nOwner: " + jda.getUserById(ConfigFile.getOwnerID())
				, "python")
				.build());
		}
	/**
	 * @return sends message to home channel
	 * @param contents
	 */
	public static void sendMessage(String contents) {
		jda.getTextChannelById("" +ConfigFile.getHomeChannel()).sendMessage(contents);
	
	}
	/**
	 * @return sends message to home channel
	 * @param message
	 */
	public static void sendMessage(Message message) {
		jda.getTextChannelById("" +ConfigFile.getHomeChannel()).sendMessage(message);
	}

}