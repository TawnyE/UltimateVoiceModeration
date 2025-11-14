package ret.tawny.ultimatevoicemoderation.logging;

import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuditLogManager {

    private final UltimateVoiceModerationPlugin plugin;
    private final File logFile;

    public AuditLogManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "audit.log");
    }

    public void logViolation(Violation violation) {
        if (!plugin.getConfig().getBoolean("logging.enabled", true)) {
            return;
        }

        plugin.getTaskScheduler().runAsync(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(violation.getTimestamp()));
                String logEntry = String.format("[%s] Player %s violated category %s with confidence %.2f. Snippet: %s",
                        timestamp, violation.getPlayerUuid(), violation.getCategory(), violation.getConfidence(), violation.getSnippet());
                writer.write(logEntry);
                writer.newLine();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to write to audit log: " + e.getMessage());
            }
        });
    }
}