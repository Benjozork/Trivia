package me.benjozork.trivia;

import me.benjozork.trivia.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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

public class QuestionHandler {

    private Utils utils;
    private Trivia main;

    private List<String> answers;
    private String last_winner = null;

    private boolean questionActive;

    public QuestionHandler(Trivia i) {
        this.utils = new Utils(i);
        this.main = i;
    }

    public void startQuestion(String q, List<String> a) {
        // Clear player attempts count
        main.getCommandHandler().clearAttempts();

        // If there is no winner (first question broadcasted)
        if (last_winner == null) {
            utils.broadcastConfigMessage("no_winner");
        }

        // If we broadcast the answer, depending on if there was one and if it is enabled in the config
        if (answers != null && main.getConfig().getBoolean("give_answer")) {
            if (main.getConfig().getBoolean("give_all_answers") && answers.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < answers.size(); i++) {
                    if (i == 0) sb.append(answers.get(i) + ", ");
                    if (i > 0 && i < answers.size() - 1) sb.append(answers.get(i) + ", ");
                    if (i == answers.size() - 1) sb.append(answers.get(i) + ".");
                }
                utils.broadcastConfigMessage("answers_were", sb.toString());
            } else {
                utils.broadcastConfigMessage("answer_was", answers.get(0));
            }
        }

        this.last_winner = null;
        this.answers = a;
        this.questionActive = true;

        utils.broadcastConfigMessage("question_starting");
        utils.broadcastConfigMessage("question_is", q);
    }



    public void winQuestion(CommandSender sender) {
        last_winner = sender.getName();
        utils.sendConfigMessage("answer.correct", sender);
        utils.broadcastConfigMessage("winner_is", sender.getName());

        if (answers != null && main.getConfig().getBoolean("give_answer")) {
            if (main.getConfig().getBoolean("give_all_answers") && answers.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < answers.size() - 1; i++) {
                    if (i == 0) sb.append(answers.get(i)).append(", ");
                    if (i > 0 && i < answers.size() - 1) sb.append(answers.get(i)).append(", ");
                    if (i == answers.size() - 1) sb.append(answers.get(i)).append(".");
                }
                utils.broadcastConfigMessage("answers_were", sb.toString());
            } else {
                utils.broadcastConfigMessage("answer_was", answers.get(0));
            }
        }

        if (sender instanceof Player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    main.getConfig().getString("command").replaceAll("%PLAYER%", sender.getName()));
        }

        Player cast_sender = null;
        UUID cast_sender_uuid = null;

        if (sender instanceof  Player) {
            cast_sender = (Player) sender;
            cast_sender_uuid = cast_sender.getUniqueId();
        }

        if (cast_sender != null) {
            if (main.getDataConfig().getConfig().get(cast_sender_uuid.toString()) == null) {
                main.getDataConfig().getConfig().set(cast_sender_uuid.toString(), 1);
            } else {
                main.getDataConfig().getConfig().set (
                        cast_sender_uuid.toString(),
                        main.getDataConfig().getConfig().getInt(cast_sender_uuid.toString()) + 1
                );
            }
            main.getDataConfig().saveConfig();
        }

        this.questionActive = false;
    }

    public void loseQuestion(CommandSender sender) {
        utils.sendConfigMessage("answer.incorrect", sender);
    }

    public boolean isCorrect(String a) {
        for (String s : answers) {
            if (a.toLowerCase().contains(s.toLowerCase())) return true;
        }
        return false;
    }

    public boolean isQuestionActive() {
        return questionActive;
    }
}
