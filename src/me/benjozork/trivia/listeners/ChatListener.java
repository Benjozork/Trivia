package me.benjozork.trivia.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.handlers.MessageHandler;
import me.benjozork.trivia.handlers.QuestionHandler;

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
public class ChatListener implements Listener {

    private HashMap<Player, Integer> attempts = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission("trivia.answer") || ! Trivia.getInstance().getConfig().getBoolean("search_for_answers_in_chat")) {
            return;
        }

        // Check if a question is active

        if (QuestionHandler.isQuestionActive()) {
            // Check if player has attempts left and increment that number
            if (Trivia.getInstance().getConfig().getInt("max_attempts") > 0) {
                attempts.putIfAbsent(player, 1);
                if (attempts.get(player) > Trivia.getInstance().getConfig().getInt("max_attempts")) {
                    player.sendMessage(MessageHandler.getMessage("no_more_attempts"));
                    return;
                } else incrementAttempts(player);
            }

            String answer = e.getMessage();

            if (QuestionHandler.isCorrect(answer)) {
                e.setMessage(e.getMessage() + MessageHandler.getRawMessage("chat_answer_correct"));

                Bukkit.getScheduler().runTaskLater(Trivia.getInstance(), () -> QuestionHandler.winQuestion(player, true), 1);

            } else {
                e.setMessage(e.getMessage() + MessageHandler.getRawMessage("chat_answer_incorrect"));
            }
        }
    }

    private void incrementAttempts(Player p) {
        int temp = attempts.get(p);
        attempts.remove(p);
        attempts.put(p, temp + 1);
    }

    protected void clearAttempts() {
        attempts.clear();
    }

}