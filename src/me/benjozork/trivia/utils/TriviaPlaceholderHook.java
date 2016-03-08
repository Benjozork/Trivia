package me.benjozork.trivia.utils;

import me.benjozork.trivia.Trivia;

import me.clip.placeholderapi.external.EZPlaceholderHook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

public class TriviaPlaceholderHook extends EZPlaceholderHook {

    private Trivia main;

    public TriviaPlaceholderHook(Plugin plugin) {
        super(plugin, "trivia");
        main = (Trivia) plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (player == null) return "";

        if (s.equalsIgnoreCase("wins")) {
            return main.getDataConfig().getConfig().getInt(player.getUniqueId().toString()) + "";
        }

        return null;
    }
}
