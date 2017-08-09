package me.benjozork.trivia.handlers.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.benjozork.trivia.Trivia;
import me.benjozork.trivia.handlers.MessageHandler;

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
public class ConfigEditor implements Listener {

    private static HashMap<Player, HashMap<String, Object>> caughtPlayers = new HashMap<>();

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {

        // Ignore players who are not editing the config

        if (! caughtPlayers.keySet().contains(e.getPlayer())) return;

        // Prevent message from being broadcasted in chat

        e.setCancelled(true);

        // Catch exit/stop/done/cancel

        if (e.getMessage().equalsIgnoreCase("stop")
            || e.getMessage().equalsIgnoreCase("cancel")
            || e.getMessage().equalsIgnoreCase("done")
            || e.getMessage().equalsIgnoreCase("exit")) {
            caughtPlayers.remove(e.getPlayer());
            e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.aborted"));
            Trivia.getInstance().saveConfig();
            return;
        }

        // Catch list

        if (e.getMessage().equalsIgnoreCase("list")) {
            e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.list_header"));
            StringBuilder parameters = new StringBuilder();
            parameters.append(MessageHandler.getRawMessage("edit_config.list_color_code"));
            final List<String> keys = new ArrayList<>(Trivia.getInstance().getConfig().getKeys(true));
            for (int i = 0; i < keys.size(); i++) {
                if (i != keys.size() - 1) parameters.append(keys.get(i)).append(", ");
                else parameters.append(keys.get(i)).append(".");
            }
            e.getPlayer().sendMessage(parameters.toString());
            return;
        }

        // Catch value checking

        if (! e.getMessage().trim().contains(" ")) {
            String key = e.getMessage();
            if (! Trivia.getInstance().getConfig().getKeys(true).contains(key)) {
                e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.invalid_param").replace("%param%", key));
                return;
            }
            String value = String.valueOf(Trivia.getInstance().getConfig().get(key));
            e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.value").replace("%param%", key).replace("%value%", value));
            return;
        }

        // Nothing catched, get value and key

        String configKey = e.getMessage().split(" ")[0];
        Object configValue = e.getMessage().split(" ")[1];

        // Make sure the value exists and is not config_version

        String key = e.getMessage().split(" ")[0];
        if (! Trivia.getInstance().getConfig().getKeys(true).contains(key) &&! Objects.equals(key.toLowerCase(), "config_version")) {
            e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.invalid_param").replace("%param%", key));
            return;
        }

        // Set value

        Trivia.getInstance().getConfig().set(configKey, configValue);

        e.getPlayer().sendMessage(MessageHandler.getMessage("edit_config.set_success").replace("%param%", configKey).replace("%value%", (String) configValue));

    }

    public static void start(Player p) {
        caughtPlayers.put(p, new HashMap<>());
        p.sendMessage(MessageHandler.getMessage("edit_config.edit_header"));
        p.sendMessage(MessageHandler.getMessage("edit_config.value_header"));

    }

}