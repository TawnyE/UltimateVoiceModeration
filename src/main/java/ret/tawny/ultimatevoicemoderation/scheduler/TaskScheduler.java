package ret.tawny.ultimatevoicemoderation.scheduler;

import java.util.concurrent.TimeUnit;

public interface TaskScheduler {

    void runAsync(Runnable task);

    void runSync(Runnable task);

    void runDelayed(Runnable task, long delay, TimeUnit unit);

    void cancelTasks();

}