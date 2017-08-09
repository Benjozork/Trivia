package me.benjozork.trivia.handlers;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.object.TriviaQuestion;
import me.benjozork.trivia.handlers.MessageHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
public class QuestionHandler {

    private static BukkitTask currentQuestionTask;
    private static TriviaQuestion currentQuestion;
    private static int question_index;

    private static String last_winner = null;

    private static boolean questionActive = false;

    private static boolean skipped = false;

    public static void start() {
        currentQuestionTask = Bukkit.getScheduler().runTaskTimer(Trivia.getInstance(), () -> {

            FileConfiguration questions_config = Trivia.getQuestionsConfig().getConfig();

            List<TriviaQuestion> questions = (List<TriviaQuestion>) questions_config.get("questions");

            if (Trivia.getInstance().getConfig().getInt("minimum_players") <= Trivia.getInstance().getServer().getOnlinePlayers().size() && Trivia.getInstance().enabled) {
                if (question_index >= questions.size())  {
                    question_index = 0;
                }

                startQuestion(questions.get(question_index));

                question_index++;
            }
        }, 0, Trivia.getInstance().getConfig().getLong("delay") * 20);
    }

    public static void skip() {
        currentQuestionTask.cancel();
        skipped = true;
        start();
    }

    public static void startQuestion(TriviaQuestion question) {

        currentQuestion = question;

        // Clear player attempts count

        Trivia.getInstance().getCommandHandler().clearAttempts();

        // If there is no winner (first question broadcasted)

        if (last_winner == null && !skipped) {
            Bukkit.broadcastMessage(MessageHandler.getMessage("no_winner"));
        }

        skipped = false;

        // If we broadcast the answer, depending on if there was one and if it is enabled in the config

        if (question.answers != null && Trivia.getInstance().getConfig().getBoolean("give_answer")) {
            if (Trivia.getInstance().getConfig().getBoolean("give_all_answers") && question.answers.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < question.answers.size(); i++) {
                    if (i < question.answers.size() - 1) sb.append(question.answers.get(i)).append(", ");
                    if (i == question.answers.size() - 1) sb.append(question.answers.get(i)).append(".");
                }

                Bukkit.broadcastMessage(MessageHandler.getMessage("answers_were").replace("%answers%", sb.toString()));
            } else {
                Bukkit.broadcastMessage(MessageHandler.getMessage("answer_was").replace("%answer%", question.answers.get(0)));
            }
        }

        //Start new question

        last_winner = null;
        questionActive = true;

        Bukkit.broadcastMessage(MessageHandler.getMessage("question_starting"));
        Bukkit.broadcastMessage(MessageHandler.getMessage("question_is").replace("%question%", question.questionText));
    }



    public static void winQuestion(CommandSender sender, boolean chat) {
        last_winner = sender.getName();
        if (!chat) sender.sendMessage(MessageHandler.getMessage("answer.correct"));
        sender.sendMessage(MessageHandler.getMessage("winner_is").replace("%name%", sender.getName()));

        if (currentQuestion.answers != null && Trivia.getInstance().getConfig().getBoolean("give_answer")) {
            if (Trivia.getInstance().getConfig().getBoolean("give_all_answers") && currentQuestion.answers.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < currentQuestion.answers.size() - 1; i++) {
                    if (i == 0) sb.append(currentQuestion.answers.get(i)).append(", ");
                    if (i > 0 && i < currentQuestion.answers.size() - 1) sb.append(currentQuestion.answers.get(i)).append(", ");
                    if (i == currentQuestion.answers.size() - 1) sb.append(currentQuestion.answers.get(i)).append(".");
                }
                Bukkit.broadcastMessage(MessageHandler.getMessage("answers_were").replace("%answers%", sb.toString()));
            } else {
                Bukkit.broadcastMessage(MessageHandler.getMessage("answer_was").replace("%answer%", currentQuestion.answers.get(0)));
            }
        }

        //Exeute the commands set in the config.

        if (sender instanceof Player) {
            if (!currentQuestion.randomCommands) {
                for (String s : currentQuestion.winnerCommands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.toLowerCase().replace("%player%", sender.getName()));
                }
            } else {
                Random random = new Random();
                for (int i = 0; i < currentQuestion.randomCommandsCount; i++) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), currentQuestion.winnerCommands.get(random.nextInt(currentQuestion.winnerCommands.size())).toLowerCase().replace("%player%", sender.getName()));
                }
            }
        }

        //Store player stats

        Player player = null;
        UUID player_uuid = null;

        if (sender instanceof  Player) {
            player = (Player) sender;
            player_uuid = player.getUniqueId();
        }

        if (player != null) {
            final String key = player_uuid.toString() + "." + currentQuestion.uuid;
            if (Trivia.getDataConfig().getConfig().get(key) == null) {
                Trivia.getDataConfig().getConfig().set(key, 1);
            } else {
                Trivia.getDataConfig().getConfig().set (
                        key,
                        Trivia.getDataConfig().getConfig().getInt(key) + 1
                );
            }
            Trivia.getDataConfig().saveConfig();
        }

        questionActive = false;
    }

    public static void winQuestionDelayed(Player player) {
    }

    public static void loseQuestion(CommandSender sender) {
        sender.sendMessage(MessageHandler.getMessage("answer.incorrect"));
    }

    public static boolean isCorrect(String a) {
        for (String s : currentQuestion.answers) {
            if (a.toLowerCase().contains(s.toLowerCase())) return true;
        }
        return false;
    }

    public static boolean isQuestionActive() {
        return questionActive;
    }

    public static TriviaQuestion currentQuestion() {
        return currentQuestion;
    }

    public static List<TriviaQuestion> loadedQuestions() {
        return (ArrayList<TriviaQuestion>) Trivia.getInstance().getQuestionsConfig().getConfig().get("questions");
    }

}