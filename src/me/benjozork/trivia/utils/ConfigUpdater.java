package me.benjozork.trivia.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjozork on 2016-11-02.
 */
public class ConfigUpdater {

    public static void updateConfig(FileConfiguration config) {
        if (config.getInt("config_version")< 2) {
            //Update config from version 1 to version 2
            String command_bak = config.getString("command");
            List<String> commands = new ArrayList<>();

            config.set("command", null);
            commands.add(command_bak);

            config.set("commands", commands);

            System.out.println("[Trivia] Config updated successfully.");
        }
    }

}
