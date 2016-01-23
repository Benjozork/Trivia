package me.benjozork.randomtrivia;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 Looks like you decompiled my code :) Don't worry, you have to right to do so.

 The MIT License (MIT)

 Copyright (c) 2016 Benjozork

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/

public class CommandHandler implements CommandExecutor {

    private RandomTrivia main;
    private Utils utils;
    private QuestionHandler qh = new QuestionHandler();

    private Economy ec;

    public CommandHandler(RandomTrivia instance) {
        this.main = instance;
        this.utils = new Utils(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("trivia")) {
            if (args.length == 0) {
                utils.sendMessage(sender, "plugin_info");
                return false;
            }

            if (qh.isQuestionActive()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++ ) {
                    if (i > 0) {
                        sb.append(" " + args[i]);
                    } else {
                        sb.append(args[i]);
                    }
                }

                if (qh.isCorrect(sb.toString())) {
                    utils.sendMessage(sender, "answer_correct");
                    utils.sendMessage(sender, "reward_won");
                    if (main.getConfig().getString("reward").equalsIgnoreCase("money")) {
                        rewardPlayer((Player) sender, main.getConfig().getInt("reward_amount"));
                        return true;
                    } else if (main.getConfig().getString("reward").equalsIgnoreCase("items")) {
                        rewardPlayer((Player) sender,
                                new ItemStack(Material.getMaterial(main.getConfig().getString("reward_material")), 12));
                        return true;
                    }
                } else {
                    utils.sendMessage(sender, "answer_incorrect");
                    return false;
                }

            } else {
                utils.sendMessage(sender, "no_question_active");
                return false;
            }
        }
        return false;
    }

    private void rewardPlayer(Player p, int a) {
        ec.depositPlayer(p, a);
    }

    private void rewardPlayer(Player p, ItemStack is) {
        p.getInventory().addItem(is);
    }

}
