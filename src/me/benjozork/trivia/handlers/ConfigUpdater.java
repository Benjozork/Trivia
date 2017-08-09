package me.benjozork.trivia.handlers;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.Utils;
import me.benjozork.trivia.object.TriviaQuestion;

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
public class ConfigUpdater {

    public static void updateConfigs(FileConfiguration config, ConfigAccessor questionsConfig, ConfigAccessor messagesConfig, ConfigAccessor playerDataConfig, Trivia trivia) {

        if (config.getInt("config_version") == 1) {

            System.out.println("[Trivia] >> Version 2 update starting...");

            Trivia.getInstance().getLogger().info(">>> Converting command to list format...");

            // Convert to multiple command support

            String command_bak = config.getString("command");
            List<String> commands = new ArrayList<>();

            config.set("command", null);
            commands.add(command_bak);

            config.set("commands", commands);

            Trivia.getInstance().getLogger().info(">>> Transfering messages...");

            // Custom message extraction fails everytime because of yaml API subtleties

            config.set("messages", "All the messages have been transfered to messages.yml automatically. Unfortunately, there was a problem while extracting your custom messages, so those will have to be redone.");

            config.set("config_version", 2);

            Trivia.getInstance().saveConfig();

            System.out.println("[Trivia] >>> Main config updated...");

            // Update question storage format

            System.out.println("[Trivia] >>> Updating question storage to object-based...");

            List<TriviaQuestion> newQuestions = new ArrayList<>();

            for (int i = 0; i < questionsConfig.getConfig().getStringList("questions").size(); i++) {
                TriviaQuestion tmp = new TriviaQuestion();
                tmp.questionText = questionsConfig.getConfig().getStringList("questions").get(i);
                tmp.answers = Utils.processAnswerTable(questionsConfig.getConfig().getStringList("answers").get(i));
                tmp.winnerCommands = Trivia.getInstance().getConfig().getStringList("commands");
                newQuestions.add(tmp);
            }

            config.set("commands", null);
            config.set("command", null);
            Trivia.getInstance().saveConfig();

            questionsConfig.getConfig().set("questions", newQuestions);
            questionsConfig.getConfig().set("answers", null);

            questionsConfig.saveConfig();

            // Update data storage to question-specific format

            System.out.println("[Trivia] >>> Updating player data storage to question-specific format...");

            for (String s : playerDataConfig.getConfig().getKeys(true)) {
                int tmp = playerDataConfig.getConfig().getInt(s);
                playerDataConfig.getConfig().set(s, null);
                playerDataConfig.getConfig().set(s + ".other", tmp);
            }

            playerDataConfig.saveConfig();

            System.out.println("[Trivia] >>> Success !");

        }

        if (config.getInt("config_version") == 2) {

            System.out.println("[Trivia] >> Version 3 update starting...");

            System.out.println("[Trivia] >>> Adding random command capability support...");

            List<TriviaQuestion> configQuestions = (ArrayList<TriviaQuestion>) questionsConfig.getConfig().get("questions");

            for (TriviaQuestion question : configQuestions) {
                question.randomCommands = false;
                question.randomCommandsCount = 2;
            }

            questionsConfig.getConfig().set("questions", configQuestions);
            questionsConfig.saveConfig();

            config.set("config_version", 3);

            Trivia.getInstance().saveConfig();

            System.out.println("[Trivia] >>> Success !");

        }

    }

    public static void checkQuestionUUIDs(ConfigAccessor questionsConfig) {

        final List<TriviaQuestion> questions = (List<TriviaQuestion>) questionsConfig.getConfig().get("questions");

        questionsConfig.getConfig().set("questions", questions);

        // Check for UUID collisions and fix them

        List<UUID> previousUUIDs = new ArrayList<>();
        for (TriviaQuestion question : questions) {
            if (previousUUIDs.contains(question.uuid)) question.uuid = UUID.randomUUID();
            previousUUIDs.add(question.uuid);
        }

        questionsConfig.saveConfig();
    }

}