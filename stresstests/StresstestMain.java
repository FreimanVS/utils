import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StresstestMain {

    private static final ExecutorService es = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        main();
    }

    private static void main() {
        firstTask();
        sleep();
        secondTask();
    }

    private static void firstTask() {
        es.submit(() -> {
            StresstestService.execute(
                    () -> {
                        System.out.println("hi " + Thread.currentThread().getName());
                    },
                    1
            );
        });
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void secondTask() {
        es.submit(() -> {
            StresstestService.execute(
                    () -> {
                        System.out.println("hi" + Thread.currentThread().getName());
                    },
                    2
            );
        });
    }
}
