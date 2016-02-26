package me.benjozork.randomtrivia;

import me.benjozork.randomtrivia.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

public class CommandHandler implements CommandExecutor {

    private final QuestionHandler qh;
    private Utils utils;
    private Trivia main;


    public CommandHandler(Trivia i, QuestionHandler qh) {
        this.qh = qh;
        this.main = i;
        this.utils = new Utils(i);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length == 0) {
                utils.sendConfigMessage("plugin_info", sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("top") && args.length == 1) {
                utils.sendConfigMessage("top_players_header", sender);
                utils.displayTopPlayersTable(sender);
                return true;
            }

            if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) && args.length == 1) {
                main.reloadConfigs();
                utils.sendConfigMessage("reload_complete", sender);
                return true;
            }

            if (qh.isQuestionActive()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        sb.append(" " + args[i]);
                    } else {
                        sb.append(args[i]);
                    }
                }

                /*if (qh.isCorrect(sb.toString())) {
                    qh.winQuestion(sender);
                } else {
                    qh.loseQuestion(sender);
                }*/
            } else {
                utils.sendConfigMessage("no_question", sender);
            }
        return false;
    }
}
