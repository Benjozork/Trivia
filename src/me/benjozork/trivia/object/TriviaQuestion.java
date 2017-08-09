package me.benjozork.trivia.object;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
@SerializableAs("TriviaQuestion")
public class TriviaQuestion implements ConfigurationSerializable {

    public TriviaQuestion(Map<String, Object> serialized) {
        this.questionText = (String) serialized.get("question_text");
        this.answers = (List<String>) serialized.get("answers");
        if (serialized.get("random_commands") != null)this.randomCommands = (boolean) serialized.get("random_commands");
        if (serialized.get("random_commands_count") != null)this.randomCommandsCount = (int) serialized.get("random_commands_count");
        this.winnerCommands = (List<String>) serialized.get("winner_commands");
        if (serialized.get("uuid") != null) this.uuid = UUID.fromString((String) serialized.get("uuid"));
        else this.uuid = UUID.randomUUID();
    }

    public TriviaQuestion() {

    }

    public String questionText;

    public List<String> answers = new ArrayList<>();

    public boolean randomCommands = false;

    public int randomCommandsCount = 0;

    public List<String> winnerCommands = new ArrayList<>();

    public UUID uuid = UUID.randomUUID();

    public boolean checkAnswer(String attempt) {
        for (String s : answers) {
            if (attempt.contains(s)) return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("question_text", this.questionText);
        ret.put("answers", this.answers);
        ret.put("random_commands", this.randomCommands);
        ret.put("random_commands_count", this.randomCommandsCount);
        ret.put("winner_commands", this.winnerCommands);
        ret.put("uuid", this.uuid.toString());
        return ret;
    }

}