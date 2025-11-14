package ret.tawny.ultimatevoicemoderation.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

import java.util.concurrent.TimeUnit;

public class BukkitTaskScheduler implements TaskScheduler {

    private final UltimateVoiceModerationPlugin plugin;

    public BukkitTaskScheduler(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public void runDelayed(Runnable task, long delay, TimeUnit unit) {
        long ticks = unit.toMillis(delay) / 50;
        plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);
    }

    @Override
    public void cancelTasks() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }
}