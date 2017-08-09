package me.benjozork.trivia;

import me.benjozork.trivia.handlers.ConfigAccessor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

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
public class Utils {

    private static Trivia main = Trivia.getInstance();
    private static ConfigAccessor data = Trivia.player_data_config;
    private static String prefix = Trivia.messages_config.getConfig().getString("prefix");

    public static List<String> processAnswerTable(String ls) {
        List<String> result = new ArrayList<>();

        ls = ls.replace("[", "");
        ls = ls.replace("]", "");

        for (String s : ls.split(",")) {
            result.add(s.trim());
        }

        return result;
    }

    public static void displayTopPlayersTable(CommandSender sender) {
        HashMap<String, HashMap<String, Integer>> raw_data = new HashMap<>();

        for (String s : data.getConfig().getKeys(false)) {
            HashMap<String, Integer> individualQuestionResults = new HashMap<>();
            for (String s1 : data.getConfig().getConfigurationSection(s).getKeys(true)) {
                individualQuestionResults.put(s1, data.getConfig().getInt(s + "." + s1));
            }
            raw_data.put(s, individualQuestionResults);
        }

        HashMap<String, Integer> totals = new HashMap<String, Integer>();

        for (String s : raw_data.keySet()) {
            int total = 0;
            for (Map.Entry<String, Integer> hashMap : raw_data.get(s).entrySet()) {
                total += hashMap.getValue();
            }
            totals.put(s, total);
        }

        final List<String> sorted_players = totals.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int count = 1;
        for (String s : sorted_players) {
            String player_name;
            try {
                player_name = Bukkit.getServer().getOfflinePlayer(UUID.fromString(s)).getName();
            } catch (Exception e) {
                player_name = s;
            }
            if (count > main.getConfig().getInt("max_leaderboard_entries")) return;
            sender.sendMessage (
                    "       "
                    + ChatColor.GREEN
                    + "#"
                    + count
                    + ": "
                    + ChatColor.AQUA
                    + player_name
                    + ChatColor.GREEN
                    + " ("
                    + totals.get(s)
                    + ")"
            );
            count++;
        }
    }

    public static void sendJSONMessage(String player, String jsonmsg) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player + " " + jsonmsg);
    }

}