package me.benjozork.trivia;

import me.benjozork.trivia.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

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
    private HashMap<Player, Integer> attempts = new HashMap<>();


    public CommandHandler(Trivia i) {
        this.qh = i.getQuestionHandler();
        this.main = i;
        this.utils = new Utils(i);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //No arguments, show plugin info
        if (args.length == 0) {
            utils.sendConfigMessage("plugin_info", sender);
            return true;
        }

        if (args.length == 1) { //Subcommand
            //Leaderboard subcomamnd
            if (args[0].equalsIgnoreCase("top")) {
                if (!sender.hasPermission("trivia.top")) {
                    utils.sendConfigMessage("permission.no_permission_top", sender);
                    return false;
                }
                utils.sendConfigMessage("top_players_header", sender);
                utils.displayTopPlayersTable(sender);
                return true;
            }

            //Reload subcommand
            if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
                if (!sender.hasPermission("trivia.reload")) {
                    utils.sendConfigMessage("permission.no_permission_reload", sender);
                    return false;
                }
                main.reloadConfigs();
                utils.sendConfigMessage("reload_complete", sender);
                return true;
            }

            //Toggle subcommand
            if ((args[0].equalsIgnoreCase("toggle"))) {
                if (!sender.hasPermission("trivia.toggle")) {
                    utils.sendConfigMessage("permission.no_permission_toggle", sender);
                    return false;
                }
                utils.sendConfigMessage(main.toggle(), sender);
                return true;
            }
        }

        //Not a subcommand, check for answer permission
        if (!sender.hasPermission("trivia.answer")) {
            utils.sendConfigMessage("permission.no_permission_answer", sender);
            return false;
        }

        if (sender instanceof Player && main.getConfig().getInt("max_attempts") > 0) {
            Player p = (Player) sender;
            attempts.putIfAbsent(p, 1);
            if (attempts.get(p) > main.getConfig().getInt("max_attempts")) {
                utils.sendConfigMessage("no_more_attempts", p);
                return false;
            } else incrementAttempts(p);
        }

        //Check if a question is active
        if (qh.isQuestionActive()) {
            //Build answer string from args
            String answer = "";
            for (String s : args) answer += (" " + s);

            if (qh.isCorrect(answer)) {
                qh.winQuestion(sender);
            } else {
                qh.loseQuestion(sender);
            }
        } else {
            //No question is active
            utils.sendConfigMessage("no_question", sender);
        }
        return false;
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
