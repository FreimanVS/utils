import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class StresstestService {

    private static ScheduledExecutorService ses;
    private static CountDownLatch countDownLatch;
    private static List<ScheduledFuture<?>> scheduledFutures;

    public static void execute(Runnable task, int tasksPerSecond) {
        stopPreviousTask();
        startNewTask(task, tasksPerSecond);
    }

    private static void stopPreviousTask() {
        if (Objects.nonNull(ses) && Objects.nonNull(scheduledFutures))
            close(scheduledFutures);
    }

    private static void startNewTask(Runnable task, int tasksPerSecond) {
        ses = Executors.newScheduledThreadPool(tasksPerSecond * 2);
        countDownLatch = new CountDownLatch(1);
        scheduledFutures = start(task, tasksPerSecond);
//        sleepFor(5L);
//        close(scheduledFutures);
    }

    private static List<ScheduledFuture<?>> start(Runnable task, int tasksPerSecond) {
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (int i = 0; i < tasksPerSecond; i++) {
            ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long timeBefore = System.currentTimeMillis();

                task.run();

                long timeAfter= System.currentTimeMillis();
                long timeTotal = timeAfter - timeBefore;
                System.out.printf("The task was completed for %s ms.%s", timeTotal, System.lineSeparator());

            }, 0L, 1, TimeUnit.SECONDS);
            scheduledFutures.add(scheduledFuture);
        }

        countDownLatch.countDown();

        return scheduledFutures;
    }

    private static void sleepFor(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void close(List<ScheduledFuture<?>> scheduledFutures) {
        cancelTasks(scheduledFutures);
        closeExecutorService(ses);
    }

    private static void cancelTasks(List<ScheduledFuture<?>> scheduledFutures) {
        scheduledFutures.forEach(StresstestService::cancelScheduledTaskAndInterrupt);
    }

    private static void cancelScheduledTaskAndInterrupt(ScheduledFuture<?> future) {
        future.cancel(true);
        try {
            if (!future.isCancelled() || !future.isDone())
                future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            System.out.println("Could not cancel a task for 60 seconds");
        }
    }

    private static void closeExecutorService(ExecutorService es) {
        try {
            es.shutdown();
            es.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println(es + " termination interrupted");
        } finally {
            if (!es.isTerminated()) {
                System.err.println(es + " killing non-finished tasks");
                es.shutdownNow();
            } else {
                System.out.println(es + " terminated");
            }

        }
    }
}
