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
 */
@SerializableAs("TriviaQuestion")
public class TriviaQuestion implements ConfigurationSerializable {

    public TriviaQuestion(Map<String, Object> serialized) {
        this.questionText = (String) serialized.get("question_text");
        this.answers = (List<String>) serialized.get("answers");
        this.winnerCommands = (List<String>) serialized.get("winner_commands");
        if (serialized.get("uuid") != null) this.uuid = UUID.fromString((String) serialized.get("uuid"));
        else this.uuid = UUID.randomUUID();
    }

    public TriviaQuestion() {

    }

    public String questionText;

    public List<String> answers = new ArrayList<>();

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
        ret.put("winner_commands", this.winnerCommands);
        ret.put("uuid", this.uuid.toString());
        return ret;
    }

}