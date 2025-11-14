package ret.tawny.ultimatevoicemoderation.voice;

import org.bukkit.event.Listener;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.ModerationEngine;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;
import ret.tawny.ultimatevoicemoderation.transcription.LocalTranscriptionProvider;
import ret.tawny.ultimatevoicemoderation.transcription.VoiceTranscriptionProvider;

import java.util.Optional;
import java.util.UUID;

public class VoiceSessionListener implements Listener {

    private final UltimateVoiceModerationPlugin plugin;
    private final ModerationEngine moderationEngine;
    private final VoiceTranscriptionProvider transcriptionProvider;

    public VoiceSessionListener(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        this.moderationEngine = plugin.getModerationEngine();
        this.transcriptionProvider = new LocalTranscriptionProvider();
    }

    // In a real implementation, this would be an event handler for a voice chat plugin's event.
    public void onPlayerSpeaking(UUID playerUuid, byte[] audioData) {
        if (!plugin.getConfigManager().isVoiceModerationEnabled()) {
            return;
        }

        plugin.getTaskScheduler().runAsync(() -> {
            transcriptionProvider.transcribe(audioData).thenAccept(transcript -> {
                Optional<Violation> violation = moderationEngine.processText(playerUuid, transcript);
                if (violation.isPresent()) {
                    plugin.getAuditLogManager().logViolation(violation.get());
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getActionManager().dispatchActions(violation.get());
                    });
                }
            });
        });
    }
}
