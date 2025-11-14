package ret.tawny.ultimatevoicemoderation.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

public class UvmCommandHandler implements CommandExecutor {

    private final UltimateVoiceModerationPlugin plugin;

    public UvmCommandHandler(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /uvm <reload|inspect|mutevoice|test>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.getConfigManager().loadConfig();
                plugin.getPatternRepository().loadPatterns();
                sender.sendMessage("Configuration and patterns reloaded.");
                break;
            case "inspect":
                sender.sendMessage("Inspect command not yet implemented.");
                break;
            case "mutevoice":
                sender.sendMessage("Mutevoice command not yet implemented.");
                break;
            case "test":
                sender.sendMessage("Test command not yet implemented.");
                break;
            default:
                sender.sendMessage("Unknown command. Usage: /uvm <reload|inspect|mutevoice|test>");
                break;
        }
        return true;
    }
}