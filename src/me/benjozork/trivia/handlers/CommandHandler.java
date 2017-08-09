package me.benjozork.trivia.handlers;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.Utils;
import me.benjozork.trivia.handlers.chat.ConfigEditor;
import me.benjozork.trivia.handlers.chat.QuestionEditor;
import me.benjozork.trivia.object.TriviaQuestion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

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
public class CommandHandler implements CommandExecutor {

    private Trivia main;
    private HashMap<Player, Integer> attempts = new HashMap<>();

    private boolean daConfirm = false;


    public CommandHandler(Trivia i) {
        this.main = i;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //No arguments, show plugin info

        if (args.length == 0) {
            sender.sendMessage(MessageHandler.getMessage("plugin_info"));
            return true;
        }

        if (args.length == 1) { // Subcommand

            // Help subcommand

            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                for (String msg : Trivia.getMessagesConfig().getConfig().getStringList("help")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }

            // Leaderboard subcomamnd

            if (args[0].equalsIgnoreCase("top") || args[0].equalsIgnoreCase("t")) {
                if (!sender.hasPermission("trivia.top")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_top"));
                    return false;
                }
                sender.sendMessage(MessageHandler.getMessage("top_players_header"));
                Utils.displayTopPlayersTable(sender);
                return true;
            }

            // Reload subcommand

            if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("r"))) {
                if (!sender.hasPermission("trivia.reload")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_reload"));
                    return false;
                }
                Trivia.getInstance().reloadConfigs();
                sender.sendMessage(MessageHandler.getMessage("reload_complete"));
                return true;
            }

            // Toggle subcommand

            if ((args[0].equalsIgnoreCase("toggle"))) {
                if (!sender.hasPermission("trivia.toggle")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_toggle"));
                    return false;
                }
                sender.sendMessage(MessageHandler.getMessage(main.toggle()));
                return true;
            }

            // Skip subcommand

            if ((args[0].equalsIgnoreCase("skip") || args[0].equalsIgnoreCase("s"))) {
                if (!sender.hasPermission("trivia.skip")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_skip"));
                    return false;
                }
                QuestionHandler.skip();
                if (Trivia.getInstance().getConfig().getBoolean("broadcast_on_skip")) {
                    Bukkit.broadcastMessage(MessageHandler.getMessage("skip").replace("%player%", sender.getName()));
                }
                return true;
            }

            // Question subcommand

            if ((args[0].equalsIgnoreCase("question") || args[0].equalsIgnoreCase("q"))) {
                if (!sender.hasPermission("trivia.question")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_question"));
                    return false;
                }
                if (QuestionHandler.isQuestionActive()) {
                    sender.sendMessage(MessageHandler.getMessage("question_is").replace("%question%", QuestionHandler.currentQuestion().questionText));
                    return true;
                } else {
                    sender.sendMessage(MessageHandler.getMessage("no_question"));
                    return true;
                }

            }

            // Add subcommand

            if ((args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a"))) {
                if (!sender.hasPermission("trivia.add")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_add"));
                    return false;
                }
                QuestionEditor.start((Player) sender);
                return true;
            }

            // List subcommand

            if ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l"))) {
                if (!sender.hasPermission("trivia.list")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_list"));
                    return false;
                }
                sender.sendMessage(MessageHandler.getMessage("question_list_header"));
                for (TriviaQuestion question : QuestionHandler.loadedQuestions()) {
                    sender.sendMessage(MessageHandler.getRawMessage("question_list_entry").replace("%question%", question.questionText));
                }
                return true;
            }

            // Deleteall subcommand

            if ((args[0].equalsIgnoreCase("deleteall") || args[0].equalsIgnoreCase("da"))) {
                if (!sender.hasPermission("trivia.deleteall")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_deleteall"));
                    return false;
                }
                sender.sendMessage(MessageHandler.getMessage("delete_all"));
                daConfirm = true;
                return true;
            }

            // Confirm subcommand

            if ((args[0].equalsIgnoreCase("confirm"))) {
                if (! daConfirm) {
                    sender.sendMessage("nothing_to_confirm");
                    return false;
                }
                if (sender instanceof Player) {
                    sender.sendMessage(MessageHandler.getMessage("confirm_console_only"));
                    return false;
                }
                Trivia.getInstance().getQuestionsConfig().getConfig().set("questions", null);
                Trivia.getInstance().getQuestionsConfig().saveConfig();
                Trivia.getInstance().reloadConfigs();
                sender.sendMessage(MessageHandler.getRawMessage("delete_all_success"));
                daConfirm = false;
                return true;
            }

            // Confirm subcommand

            if ((args[0].equalsIgnoreCase("config"))) {
                if (!sender.hasPermission("trivia.config")) {
                    sender.sendMessage(MessageHandler.getMessage("permission.no_permission_config"));
                    return false;
                }
                ConfigEditor.start((Player) sender);
                return true;
            }

        }

        // Not a subcommand, check for answer permission

        if (!sender.hasPermission("trivia.answer")) {
            sender.sendMessage(MessageHandler.getMessage("permission.no_permission_answer"));
            return false;
        }

        // Check if a question is active

        if (QuestionHandler.isQuestionActive()) {
            // Check if player has attempts left and increment that number
            if (sender instanceof Player && main.getConfig().getInt("max_attempts") > 0) {
                Player p = (Player) sender;
                attempts.putIfAbsent(p, 1);
                if (attempts.get(p) > main.getConfig().getInt("max_attempts")) {
                    p.sendMessage(MessageHandler.getMessage("no_more_attempts"));
                    return false;
                } else incrementAttempts(p);
            }

            // Build answer string from args

            String answer = "";
            for (String s : args) answer += (" " + s);

            if (QuestionHandler.isCorrect(answer)) {
                QuestionHandler.winQuestion(sender, false);
            } else {
                QuestionHandler.loseQuestion(sender);
            }
        } else {
            // No question is active
            sender.sendMessage(MessageHandler.getMessage("no_question"));
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