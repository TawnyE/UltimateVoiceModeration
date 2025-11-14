package ret.tawny.ultimatevoicemoderation.config;

import org.bukkit.configuration.file.FileConfiguration;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

public class ConfigManager {

    private final UltimateVoiceModerationPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public boolean isPluginEnabled() {
        return config.getBoolean("enabled", true);
    }

    public boolean isVoiceModerationEnabled() {
        return config.getBoolean("voice.enabled", true);
    }

    public boolean isTextModerationEnabled() {
        return config.getBoolean("text.enabled", true);
    }
}