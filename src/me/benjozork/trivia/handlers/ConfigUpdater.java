package me.benjozork.trivia.handlers;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.Utils;
import me.benjozork.trivia.object.TriviaQuestion;

/**
 * Created by Benjozork on 2016-11-02.
 */
public class ConfigUpdater {

    public static void updateConfig(FileConfiguration config, ConfigAccessor questionsConfig, ConfigAccessor messagesConfig, ConfigAccessor playerDataConfig, Trivia trivia) {

        if (config.getInt("config_version") == 1) {

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