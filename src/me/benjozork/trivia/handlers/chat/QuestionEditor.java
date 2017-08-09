package me.benjozork.trivia.handlers.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.handlers.MessageHandler;
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
public class QuestionEditor implements Listener {

    private static HashMap<Player, TriviaQuestion> caughtPlayers = new HashMap<>();
    private static List<Player> hasStartedSettingCommands = new ArrayList<>();

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {

        if (! caughtPlayers.keySet().contains(e.getPlayer())) return;
        e.setCancelled(true);
        if (e.getMessage().equalsIgnoreCase("stop") || e.getMessage().equalsIgnoreCase("cancel")) {
            caughtPlayers.remove(e.getPlayer());
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.aborted"));
            return;
        }

        final TriviaQuestion currentQuestion = caughtPlayers.get(e.getPlayer());

        boolean next = false;

        if (currentQuestion.questionText == null) {
            for (TriviaQuestion question : (List<TriviaQuestion>) Trivia.getInstance().getQuestionsConfig().getConfig().get("questions")) {
                if (e.getMessage().equalsIgnoreCase(question.questionText)) {
                    e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.title_set_fail"));
                    return;
                }
            }
            currentQuestion.questionText = e.getMessage();
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.title_set_success"));
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.set_answers"));
        }

        else if (currentQuestion.answers.isEmpty() || next) {
            List<String> answers = new ArrayList<>();
            for (String s : e.getMessage().split(",")) {
                answers.add(s.trim());
            }
            currentQuestion.answers = answers;
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.answers_set_success"));
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.set_commands"));
        }

        else {
            if (! hasStartedSettingCommands.contains(e.getPlayer())) {
                hasStartedSettingCommands.add(e.getPlayer());
            }
            if (e.getMessage().trim().equalsIgnoreCase("done")) {
                List<TriviaQuestion> configuredQuestions = (List<TriviaQuestion>) Trivia.getInstance().getQuestionsConfig().getConfig().get("questions");
                configuredQuestions.add(currentQuestion);
                Trivia.getInstance().getQuestionsConfig().getConfig().set("questions", configuredQuestions);
                Trivia.getInstance().getQuestionsConfig().saveConfig();
                e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.done"));
                caughtPlayers.remove(e.getPlayer());
                return;
            }
            currentQuestion.winnerCommands.add(e.getMessage());
            e.getPlayer().sendMessage(MessageHandler.getMessage("add_question.command_added_success"));
        }

    }

    public static void start(Player p) {
        caughtPlayers.put(p, new TriviaQuestion());
        p.sendMessage(MessageHandler.getMessage("add_question.set_title"));
    }

}