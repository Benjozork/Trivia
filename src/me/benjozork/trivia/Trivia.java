package me.benjozork.trivia;

import me.benjozork.trivia.handlers.chat.ConfigEditor;
import me.benjozork.trivia.handlers.chat.QuestionEditor;
import me.benjozork.trivia.handlers.CommandHandler;
import me.benjozork.trivia.handlers.QuestionHandler;
import me.benjozork.trivia.listeners.ChatListener;
import me.benjozork.trivia.object.TriviaQuestion;
import me.benjozork.trivia.handlers.ConfigAccessor;
import me.benjozork.trivia.handlers.ConfigUpdater;
import me.benjozork.trivia.handlers.MessageHandler;
import me.benjozork.trivia.handlers.TriviaPlaceholderHook;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * @author Benjozork

    The MIT License (MIT)

    Copyright (c) 2016-2017 Benjozork

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
    documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
    rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
    HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
    CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
    THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
public class Trivia extends JavaPlugin {

    private static Trivia instance;

    Logger log = Logger.getLogger("Minecraft");

    private CommandHandler command_handler;

    public static ConfigAccessor messages_config;
    public static ConfigAccessor questions_config;
    public static ConfigAccessor player_data_config;

    public boolean enabled = true;

    @Override
    public void onEnable() {

        /* Add defaults to config.yml */

        YamlConfiguration defConfig = null;
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        }

        for (String key : defConfig.getKeys(true)) {
            if (! getConfig().getKeys(true).contains(key)) {
                getConfig().set(key, defConfig.get(key));
            }
        }

        saveConfig();

        /**/

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuestionEditor(), this);
        Bukkit.getPluginManager().registerEvents(new ConfigEditor(), this);

        instance = (Trivia) Bukkit.getPluginManager().getPlugin("Trivia");

        ConfigurationSerialization.registerClass(TriviaQuestion.class);

        messages_config = new ConfigAccessor(getInstance(), "messages.yml");
        questions_config = new ConfigAccessor(getInstance(), "questions.yml");
        player_data_config = new ConfigAccessor(getInstance(), "data.yml");

        //messages_config.getConfig().options().copyDefaults(true);

        saveDefaultConfig();
        messages_config.saveDefaultConfig();
        questions_config.saveDefaultConfig();
        player_data_config.saveDefaultConfig();

        MessageHandler.setConfiguration(messages_config.getConfig());

        if (getConfig().getInt("config_version") < 3) {
            log.info("[Trivia] Config is outdated. Trying to update...");
            ConfigUpdater.updateConfigs(getConfig(), getQuestionsConfig(), getMessagesConfig(), getDataConfig(), this);
        }

        // Check for custom questions and generate UUIDs for them

        ConfigUpdater.checkQuestionUUIDs(getQuestionsConfig());

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TriviaPlaceholderHook(this).hook();
        }

        log.info("[Trivia] Enabled successfully.");
        if (getDescription().getVersion().contains("DEV")) {
            log.info("[Trivia] Development version! Please send bug reports to GitHub!");
        }

        command_handler = new CommandHandler(this);

        this.getCommand("trivia").setExecutor(command_handler);
        this.getCommand("tr").setExecutor(command_handler);

        QuestionHandler.start();

    }

    @Override
    public void onDisable() {
        player_data_config.saveConfig();
        log.info("[Trivia] Disabled successfully.");
    }

    public static void reloadConfigs() {
        Trivia.getInstance().reloadConfig();
        getMessagesConfig().reloadConfig();
        MessageHandler.setConfiguration(getMessagesConfig().getConfig());
        getQuestionsConfig().reloadConfig();
        ConfigUpdater.checkQuestionUUIDs(getQuestionsConfig());
        getDataConfig().reloadConfig();
        /*messages_config = new ConfigAccessor(getInstance(), "messages.yml");
        questions_config = new ConfigAccessor(getInstance(), "questions.yml");
        player_data_config = new ConfigAccessor(getInstance(), "data.yml");*/
    }

    public CommandHandler getCommandHandler() {
        return command_handler;
    }

    public static ConfigAccessor getDataConfig() {
        return player_data_config;
    }

    public static ConfigAccessor getQuestionsConfig() {
        return questions_config;
    }

    public static ConfigAccessor getMessagesConfig() {
        return messages_config;
    }

    public String toggle() {
        enabled = !enabled;
        return enabled ? "toggle.enabled" : "toggle.disabled";
    }

    public static Trivia getInstance() {
        return instance;
    }

}