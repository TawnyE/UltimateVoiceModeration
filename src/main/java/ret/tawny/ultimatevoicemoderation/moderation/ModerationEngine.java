package ret.tawny.ultimatevoicemoderation.moderation;

import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModerationEngine {

    private final UltimateVoiceModerationPlugin plugin;
    private final PatternRepository patternRepository;

    public ModerationEngine(UltimateVoiceModerationPlugin plugin, PatternRepository patternRepository) {
        this.plugin = plugin;
        this.patternRepository = patternRepository;
    }

    public Optional<Violation> processText(UUID playerUuid, String text) {
        String lowerCaseText = text.toLowerCase();

        // 1. Exact and regex patterns
        for (String word : patternRepository.getHardBannedWords()) {
            if (lowerCaseText.contains(word)) {
                return Optional.of(new Violation(playerUuid, ViolationCategory.EXPLICIT_LANGUAGE, 1.0, word));
            }
        }

        for (Pattern pattern : patternRepository.getRegexPatterns()) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Optional.of(new Violation(playerUuid, ViolationCategory.EXPLICIT_LANGUAGE, 0.9, matcher.group()));
            }
        }

        // 2. Approximate / fuzzy matching
        String[] words = text.split("\\s+");
        for (String word : words) {
            for (String softBannedWord : patternRepository.getSoftBannedWords()) {
                if (levenshteinDistance(word, softBannedWord) <= 2) {
                    return Optional.of(new Violation(playerUuid, ViolationCategory.EXPLICIT_LANGUAGE, 0.75, word));
                }
            }
        }


        return Optional.empty();
    }

    private int levenshteinDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }
}