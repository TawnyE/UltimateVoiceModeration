package ret.tawny.ultimatevoicemoderation.voice;

import org.bukkit.plugin.Plugin;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.voice.api.VoiceApi;

public class VoiceIntegrationManager {

    private final UltimateVoiceModerationPlugin plugin;
    private VoiceApi voiceApi;
    private boolean voiceChatPluginFound = false;

    public VoiceIntegrationManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        Plugin voiceChatPlugin = plugin.getServer().getPluginManager().getPlugin("voicechat");
        if (voiceChatPlugin != null && voiceChatPlugin.isEnabled()) {
            plugin.getLogger().info("Found voice chat plugin, hooking in.");
            voiceChatPluginFound = true;
            voiceApi = new VoiceApi(plugin, new VoiceSessionListener(plugin));
            voiceApi.init();
        } else {
            plugin.getLogger().warning("No voice chat plugin found. Voice moderation will be disabled.");
        }
    }

    public boolean isVoiceChatPluginFound() {
        return voiceChatPluginFound;
    }

    public VoiceApi getVoiceApi() {
        return voiceApi;
    }

    public void close() {
        if (voiceApi != null) {
            voiceApi.close();
        }
    }
}
