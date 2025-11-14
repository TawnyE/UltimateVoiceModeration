package ret.tawny.ultimatevoicemoderation.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;

import java.util.List;
import java.util.UUID;

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

        switch (actionType) {
            case WARN:
                player.sendMessage("You have been warned for: " + violation.getCategory());
                break;
            case TEMP_VOICE_MUTE:
                // In a real implementation, we would use the voice chat API to mute the player.
                plugin.getLogger().info("Temporarily voice muted player " + player.getName());
                break;
            case PERMA_VOICE_MUTE:
                // In a real implementation, we would use the voice chat API to mute the player.
                plugin.getLogger().info("Permanently voice muted player " + player.getName());
                break;
            case KICK:
                player.kickPlayer("You have been kicked for: " + violation.getCategory());
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
}