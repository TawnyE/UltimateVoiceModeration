package ret.tawny.ultimatevoicemoderation.voice;

import org.bukkit.plugin.Plugin;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

public class VoiceIntegrationManager {

    private final UltimateVoiceModerationPlugin plugin;
    private boolean voiceChatPluginFound = false;

    public VoiceIntegrationManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        Plugin voiceChatPlugin = plugin.getServer().getPluginManager().getPlugin("voicechat");
        if (voiceChatPlugin != null && voiceChatPlugin.isEnabled()) {
            plugin.getLogger().info("Found voice chat plugin, hooking in.");
            voiceChatPluginFound = true;
            // In a real implementation, we would register our voice listener here.
            // For now, we'll just simulate this.
            // plugin.getServer().getPluginManager().registerEvents(new VoiceSessionListener(plugin), plugin);
        } else {
            plugin.getLogger().warning("No voice chat plugin found. Voice moderation will be disabled.");
        }
    }

    public boolean isVoiceChatPluginFound() {
        return voiceChatPluginFound;
    }
}
