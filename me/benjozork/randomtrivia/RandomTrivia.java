package me.benjozork.randomtrivia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

public class RandomTrivia extends JavaPlugin {

    Logger log = Logger.getLogger("Minecraft");
    QuestionHandler qh = new QuestionHandler(this);
    private int question_index;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        log.info("[RandomTrivia] Enabled successfully.");
        getCommand("trivia").setExecutor(new CommandHandler(this, qh));

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if (getConfig().getInt("minimum_players") <= getServer().getOnlinePlayers().size()) {
                    if (question_index > getConfig().getStringList("questions").size() - 1 || question_index > getConfig().getStringList("answers").size() - 1) {
                        question_index = 0;
                    }
                    qh.startQuestion(getConfig().getStringList("questions").get(question_index), getConfig().getStringList("answers").get(question_index));
                    question_index++;
                }
            }
        }, 0, getConfig().getLong("interval") * 20);
    }

    @Override
    public void onDisable() {
        log.info("[RandomTrivia] Disabled successfully.");
    }

}
