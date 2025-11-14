package ret.tawny.ultimatevoicemoderation.scheduler;

import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

public class TaskSchedulerFactory {

    public static TaskScheduler createScheduler(UltimateVoiceModerationPlugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            return new FoliaTaskScheduler(plugin);
        } catch (ClassNotFoundException e) {
            return new BukkitTaskScheduler(plugin);
        }
    }
}