package ret.tawny.ultimatevoicemoderation.voice;

import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.ModerationEngine;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;
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
        this.transcriptionProvider = plugin.getVoiceTranscriptionProvider();
    }

    @EventHandler
    public void onPlayerVoicePacket(MicrophonePacketEvent event) {
        if (!plugin.getConfigManager().isVoiceModerationEnabled()) {
            return;
        }

        if (event.getSenderConnection() == null) {
            return;
        }
        UUID playerUuid = event.getSenderConnection().getPlayer().getUuid();
        byte[] audioData = event.getPacket().getOpusEncodedData();

        plugin.getTaskScheduler().runAsync(() -> {
            transcriptionProvider.transcribe(audioData).thenAccept(transcript -> {
                if (transcript.isEmpty()) {
                    return;
                }
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
