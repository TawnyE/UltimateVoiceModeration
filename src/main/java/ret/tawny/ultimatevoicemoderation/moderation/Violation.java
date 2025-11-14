package ret.tawny.ultimatevoicemoderation.moderation;

import java.util.UUID;

public class Violation {

    private final UUID playerUuid;
    private final ViolationCategory category;
    private final double confidence;
    private final String snippet;
    private final long timestamp;

    public Violation(UUID playerUuid, ViolationCategory category, double confidence, String snippet) {
        this.playerUuid = playerUuid;
        this.category = category;
        this.confidence = confidence;
        this.snippet = snippet;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public ViolationCategory getCategory() {
        return category;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getSnippet() {
        return snippet;
    }

    public long getTimestamp() {
        return timestamp;
    }
}