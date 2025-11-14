package ret.tawny.ultimatevoicemoderation.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.moderation.Violation;
import ret.tawny.ultimatevoicemoderation.util.Pagination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UvmCommandHandler implements CommandExecutor {

    private final UltimateVoiceModerationPlugin plugin;

    public UvmCommandHandler(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.usage"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.getConfigManager().loadConfig();
                plugin.getPatternRepository().loadPatterns();
                plugin.getMessageManager().loadMessages();
                sender.sendMessage(plugin.getMessageManager().getMessage("command.reload"));
                break;
            case "inspect":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.usage"));
                    return true;
                }
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                if (targetPlayer == null) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.player_not_found", "player", args[1]));
                    return true;
                }
                UUID targetUuid = targetPlayer.getUniqueId();
                int page = 1;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.invalid_page"));
                        return true;
                    }
                }
                inspectPlayer(sender, targetUuid, page);
                break;
            case "mutevoice":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.mutevoice.usage"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.mutevoice.player_not_found", "player", args[1]));
                    return true;
                }
                UUID targetMuteUuid = target.getUniqueId();
                if (plugin.getVoiceIntegrationManager().getVoiceApi().isMuted(targetMuteUuid)) {
                    plugin.getVoiceIntegrationManager().getVoiceApi().unmutePlayer(targetMuteUuid);
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.mutevoice.unmuted", "player", target.getName()));
                } else {
                    plugin.getVoiceIntegrationManager().getVoiceApi().mutePlayer(targetMuteUuid);
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.mutevoice.muted", "player", target.getName()));
                }
                break;
            case "test":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.test.usage"));
                    return true;
                }
                String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                Optional<Violation> violation = plugin.getModerationEngine().processText(null, message);
                if (violation.isPresent()) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.test.violation", "category", violation.get().getCategory().name(), "confidence", String.valueOf(violation.get().getConfidence())));
                } else {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.test.no_violation"));
                }
                break;
            default:
                sender.sendMessage(plugin.getMessageManager().getMessage("command.unknown"));
                break;
        }
        return true;
    }

    private void inspectPlayer(CommandSender sender, UUID playerUuid, int page) {
        File logFile = new File(plugin.getDataFolder(), "audit.log");
        if (!logFile.exists()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.no_history"));
            return;
        }

        List<String> violations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(playerUuid.toString())) {
                    violations.add(line);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read audit log: " + e.getMessage());
            sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.error"));
            return;
        }

        if (violations.isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.inspect.no_history"));
            return;
        }

        Pagination.sendPage(sender, violations, page, 10, "§cViolation History for " + Bukkit.getOfflinePlayer(playerUuid).getName());
    }
}
