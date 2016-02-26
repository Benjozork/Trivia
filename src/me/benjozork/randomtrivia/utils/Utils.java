package me.benjozork.randomtrivia.utils;

import me.benjozork.randomtrivia.Trivia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class Utils {

    private Trivia main;
    private ConfigAccessor data;
    private String prefix;

    public Utils(Trivia main) {
        this.main = main;
        this.data = main.getDataConfig();
        prefix = main.getConfig().getString("messages.global_prefix");
    }

    public void sendConfigMessage(String path, CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + main.getConfig().getString("messages." + path)));
    }

    public void broadcastConfigMessage(String path) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + main.getConfig().getString("messages." + path)));
    }

    public void broadcastConfigMessage(String path, String append) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + main.getConfig().getString("messages." + path) + append));
    }

    public List<String> processAnswerTable(String ls) {
        List<String> result = new ArrayList<>();

        ls = ls.replace("[", "");
        ls = ls.replace("]", "");

        for (String s : ls.split(",")) {
            result.add(s.trim());
        }

        return result;
    }

    public void displayTopPlayersTable(CommandSender sender) {
        HashMap<String, Integer> player_data = new HashMap<>();

        for (String s : data.getConfig().getKeys(false)) {
            player_data.put(s, data.getConfig().getInt(s));
        }

        final List<String> sorted_players = player_data.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int count = 1;
        for (String s : sorted_players) {
            if (count > 10) return;
            sender.sendMessage (
                    "       "
                    + ChatColor.GREEN
                    + "#"
                    + count
                    + ": "
                    + ChatColor.AQUA
                    + s
                    + ChatColor.GREEN
                    + " ("
                    + player_data.get(s)
                    + ")"
            );
            count++;
        }
    }
}
