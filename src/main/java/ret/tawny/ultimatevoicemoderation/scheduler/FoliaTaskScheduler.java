package ret.tawny.ultimatevoicemoderation.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

import java.util.concurrent.TimeUnit;

public class FoliaTaskScheduler implements TaskScheduler {

    private final UltimateVoiceModerationPlugin plugin;
    private final AsyncScheduler asyncScheduler;
    private final GlobalRegionScheduler globalRegionScheduler;

    public FoliaTaskScheduler(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        this.asyncScheduler = Bukkit.getServer().getAsyncScheduler();
        this.globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
    }

    @Override
    public void runAsync(Runnable task) {
        asyncScheduler.runNow(plugin, scheduledTask -> task.run());
    }

    @Override
    public void runSync(Runnable task) {
        globalRegionScheduler.execute(plugin, task);
    }

    @Override
    public void runDelayed(Runnable task, long delay, TimeUnit unit) {
        long ticks = unit.toMillis(delay) / 50;
        globalRegionScheduler.runDelayed(plugin, scheduledTask -> task.run(), ticks);
    }

    @Override
    public void cancelTasks() {
        asyncScheduler.cancelTasks(plugin);
        globalRegionScheduler.cancelTasks(plugin);
    }
}