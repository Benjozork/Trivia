package me.benjozork.randomtrivia;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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

    private String answer;
    private String question;

    private boolean questionActive;
    private String last_winner;
    private boolean equals_mode = false;

    public QuestionHandler(RandomTrivia i) {
        this.utils = new Utils(i);
    }

    public void startQuestion(String q, String a) {
        if (Objects.equals(last_winner, "")) {
            utils.broadcastConfigMessage("no_winner");
            if (answer != null) {
                utils.broadcastConfigMessage("answer_was", answer.replaceAll("!EQUALS", ""));
            }
        }

        last_winner = "";

        this.answer = a;
        this.question = q;
        this.questionActive = true;
        this.equals_mode = false;

        utils.broadcastConfigMessage("question_starting");
        utils.broadcastConfigMessage("question_is", question);
    }



    public void winQuestion(CommandSender sender) {
        last_winner = sender.getName();
        utils.sendConfigMessage("answer_correct", sender);
        utils.broadcastConfigMessage("winner_is", sender.getName());
        utils.broadcastConfigMessage("answer_was", answer);

        if (sender instanceof Player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    utils.getConfig().getString("command").replaceAll("%PLAYER%", sender.getName()));
        }

        this.questionActive = false;
    }

    public void loseQuestion(CommandSender sender) {
        utils.sendConfigMessage("answer_incorrect", sender);
    }

    public boolean isCorrect(String a) {
        if (answer.startsWith("!EQUALS") || equals_mode) {
            if (!equals_mode) answer = answer.substring(7);
            equals_mode = true;
            return answer.equalsIgnoreCase(a);
        } else {
            return a.equalsIgnoreCase(answer) || a.contains(answer);
        }
    }

    public boolean isQuestionActive() {
        return questionActive;
    }
}
