package ret.tawny.ultimatevoicemoderation.text;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.ModerationEngine;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;

import java.util.Optional;

public class TextChatListener implements Listener {

    private final UltimateVoiceModerationPlugin plugin;
    private final ModerationEngine moderationEngine;

    public TextChatListener(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        this.moderationEngine = plugin.getModerationEngine();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfigManager().isTextModerationEnabled()) {
            return;
        }

        String message = event.getMessage();
        Optional<Violation> violation = moderationEngine.processText(event.getPlayer().getUniqueId(), message);

        if (violation.isPresent()) {
            event.setCancelled(true);
            plugin.getAuditLogManager().logViolation(violation.get());
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getActionManager().dispatchActions(violation.get());
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("ultimatevoicemoderation.staff")) {
                        player.sendMessage(plugin.getMessageManager().getMessage("staff.violation_notification",
                                "player", event.getPlayer().getName(),
                                "category", violation.get().getCategory().name()));
                    }
                });
            });
        }
    }
}
