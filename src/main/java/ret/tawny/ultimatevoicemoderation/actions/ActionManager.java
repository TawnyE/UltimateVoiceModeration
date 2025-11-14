package ret.tawny.ultimatevoicemoderation.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;
import ret.tawny.ultimatevoicemoderation.voice.api.VoiceApi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActionManager {

    private final UltimateVoiceModerationPlugin plugin;

    public ActionManager(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    public void dispatchActions(Violation violation) {
        String category = violation.getCategory().name().toLowerCase();
        List<String> actions = plugin.getConfig().getStringList("categories." + category + ".actions");

        for (String action : actions) {
            try {
                ActionType actionType = ActionType.valueOf(action.toUpperCase());
                executeAction(actionType, violation);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid action type '" + action + "' in config.yml.");
            }
        }
    }

    private void executeAction(ActionType actionType, Violation violation) {
        UUID playerUuid = violation.getPlayerUuid();
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }

        VoiceApi voiceApi = plugin.getVoiceIntegrationManager().getVoiceApi();

        switch (actionType) {
            case WARN:
                player.sendMessage(plugin.getMessageManager().getMessage("player.warn", "category", violation.getCategory().name()));
                break;
            case TEMP_VOICE_MUTE:
                if (voiceApi != null) {
                    voiceApi.mutePlayer(playerUuid);
                    String durationStr = plugin.getConfig().getString("categories." + violation.getCategory().name().toLowerCase() + ".muteDuration", "10m");
                    long duration = parseDuration(durationStr);
                    plugin.getTaskScheduler().runDelayed(() -> voiceApi.unmutePlayer(playerUuid), duration, TimeUnit.MILLISECONDS);
                    player.sendMessage(plugin.getMessageManager().getMessage("player.temp_voice_mute", "category", violation.getCategory().name()));
                }
                break;
            case PERMA_VOICE_MUTE:
                if (voiceApi != null) {
                    voiceApi.mutePlayer(playerUuid);
                    player.sendMessage(plugin.getMessageManager().getMessage("player.perma_voice_mute", "category", violation.getCategory().name()));
                }
                break;
            case KICK:
                player.kickPlayer(plugin.getMessageManager().getMessage("player.kick", "category", violation.getCategory().name()));
                break;
            case LOG_ONLY:
                // This action is handled by the AuditLogManager.
                break;
            case RUN_COMMANDS:
                String category = violation.getCategory().name().toLowerCase();
                List<String> commands = plugin.getConfig().getStringList("categories." + category + ".consoleCommands");
                for (String command : commands) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                }
                break;
        }
    }

    private long parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return 0;
        }

        long duration = 0;
        try {
            char unit = durationStr.charAt(durationStr.length() - 1);
            long value = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            switch (unit) {
                case 's':
                    duration = value * 1000;
                    break;
                case 'm':
                    duration = value * 60000;
                    break;
                case 'h':
                    duration = value * 3600000;
                    break;
                case 'd':
                    duration = value * 86400000;
                    break;
                default:
                    duration = Long.parseLong(durationStr) * 1000;
                    break;
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid duration format: " + durationStr);
        }
        return duration;
    }
}
