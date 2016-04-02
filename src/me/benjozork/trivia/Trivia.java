package me.benjozork.trivia;

import me.benjozork.trivia.utils.ConfigAccessor;
import me.benjozork.trivia.utils.TriviaPlaceholderHook;
import me.benjozork.trivia.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 Looks like you decompiled my code :) Don't worry, you have to right to do so.

 The MIT License (MIT)

 Copyright (c) 2016 Benjozork

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

    Logger log = Logger.getLogger("Minecraft");

    private QuestionHandler question_handler = new QuestionHandler(this);
    private CommandHandler command_handler = new CommandHandler(this);

    private ConfigAccessor questions_config = new ConfigAccessor(this, "questions.yml");
    private ConfigAccessor player_data = new ConfigAccessor(this, "data.yml");

    private Utils utils = new Utils(this);

    private List<List<String>> answers = new ArrayList<>();
    private int question_index;
    private boolean enabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        questions_config.saveDefaultConfig();
        player_data.saveDefaultConfig();

        getConfig().options().copyDefaults(true);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TriviaPlaceholderHook(this).hook();
        }

        log.info("[Trivia] Enabled successfully.");
        if (getDescription().getVersion().contains("DEV")) {
            log.info("[Trivia] Development version! Please send bug reports to GitHub!");
        }

        getCommand("trivia").setExecutor(command_handler);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                answers = new ArrayList<>();
                if (getConfig().getInt("minimum_players") <= getServer().getOnlinePlayers().size() && enabled) {
                    if (question_index > questions_config.getConfig().getStringList("questions").size() - 1 || question_index > questions_config.getConfig().getList("answers").size() - 1) {
                        question_index = 0;
                    }

                    for (int i = 0; i < questions_config.getConfig().getStringList("answers").size(); i++) {
                        answers.add (
                                utils.processAnswerTable(questions_config.getConfig()
                                .getStringList("answers").get(i))
                        );
                    }

                    question_handler.startQuestion (
                            questions_config.getConfig().getStringList("questions").get(question_index),
                            answers.get(question_index)
                    );


                    question_index++;
                }
            }
        }, 0, getConfig().getLong("delay") * 20);
    }

    @Override
    public void onDisable() {
        player_data.saveConfig();
        log.info("[Trivia] Disabled successfully.");
    }

    protected void reloadConfigs() {
        reloadConfig();
        questions_config.reloadConfig();
        player_data.reloadConfig();
    }

    protected QuestionHandler getQuestionHandler() {
        return question_handler;
    }

    protected CommandHandler getCommandHandler() {
        return command_handler;
    }

    public ConfigAccessor getDataConfig() {
        return player_data;
    }

    protected String toggle() {
        enabled = !enabled;
        return enabled ? "toggle.enabled" : "toggle.disabled";
    }
}
