package ret.tawny.ultimatevoicemoderation.moderation;

import org.apache.commons.codec.language.DoubleMetaphone;
import ret.tawny.ultimatevoicemoderation.config.ConfigManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.bukkit.configuration.file.FileConfiguration;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

public class PatternRepository {

    private final UltimateVoiceModerationPlugin plugin;
    private final List<String> hardBannedWords = new ArrayList<>();
    private final List<String> softBannedWords = new ArrayList<>();
    private final List<Pattern> regexPatterns = new ArrayList<>();
    private final Map<String, List<String>> phoneticWords = new HashMap<>();
    private final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    public PatternRepository(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        doubleMetaphone.setMaxCodeLen(10);
    }

    public void loadPatterns() {
        hardBannedWords.clear();
        softBannedWords.clear();
        regexPatterns.clear();
        phoneticWords.clear();

        FileConfiguration config = plugin.getConfig();

        hardBannedWords.addAll(config.getStringList("patternLists.hardBannedWords"));
        softBannedWords.addAll(config.getStringList("patternLists.softBannedWords"));

        for (String regex : config.getStringList("patternLists.regexPatterns")) {
            try {
                regexPatterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
            } catch (PatternSyntaxException e) {
                plugin.getLogger().warning("Invalid regex pattern '" + regex + "': " + e.getMessage());
            }
        }

        for (String word : softBannedWords) {
            String phoneticCode = doubleMetaphone.encode(word);
            phoneticWords.computeIfAbsent(phoneticCode, k -> new ArrayList<>()).add(word);
        }
    }

    public List<String> getHardBannedWords() {
        return hardBannedWords;
    }

    public List<String> getSoftBannedWords() {
        return softBannedWords;
    }

    public List<Pattern> getRegexPatterns() {
        return regexPatterns;
    }

    public Map<String, List<String>> getPhoneticWords() {
        return phoneticWords;
    }
}
