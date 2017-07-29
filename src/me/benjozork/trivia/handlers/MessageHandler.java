package me.benjozork.trivia.handlers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Benjozork
 */
public class MessageHandler {

    private static FileConfiguration configuration;

    public static String getMessage(String id) {
        return ChatColor.translateAlternateColorCodes('&', configuration.getString("prefix") + " " + configuration.getString(id));
    }

    public static String getRawMessage(String id) {
        return ChatColor.translateAlternateColorCodes('&', configuration.getString(id));
    }


    public static void setConfiguration(FileConfiguration configuration) {
        MessageHandler.configuration = configuration;
    }

}