package ret.tawny.ultimatevoicemoderation.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageManager {

    private final UltimateVoiceModerationPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final File messagesFile;
    private FileConfiguration messagesConfig;

    public MessageManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
    }

    public void loadMessages() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        InputStream defaultMessagesStream = plugin.getResource("messages.yml");
        if (defaultMessagesStream != null) {
            messagesConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultMessagesStream)));
        }

        messages.clear();
        for (String key : messagesConfig.getKeys(true)) {
            if (!messagesConfig.isConfigurationSection(key)) {
                messages.put(key, messagesConfig.getString(key));
            }
        }
    }

    public String getMessage(String key, String... placeholders) {
        String message = messages.getOrDefault(key, "Unknown message: " + key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}