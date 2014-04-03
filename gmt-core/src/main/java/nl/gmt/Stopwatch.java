package nl.gmt;

public class Stopwatch {
    private boolean running;
    private long startTime;
    private long endTime;

    public static Stopwatch startNew() {
        Stopwatch result = new Stopwatch();

        result.start();

        return result;
    }

    public Stopwatch() {
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("Stopwatch is already running");
        }

        startTime = System.nanoTime();
        running = true;
    }

    public void stop() {
        if (!running) {
            throw new IllegalStateException("Stopwatch is not running");
        }

        endTime = System.nanoTime();
        running = false;
    }

    public void reset() {
        if (running) {
            stop();
        }

        startTime = 0;
        endTime = 0;
    }

    public void restart() {
        stop();
        reset();
        start();
    }

    public long elapsedTicks() {
        if (running) {
            return System.nanoTime() - startTime;
        }

        return endTime - startTime;
    }

    public long elapsedMilliseconds() {
        return elapsedTicks() / 1000000;
    }

    public Interval elapsed() {
        return new Interval(elapsedTicks());
    }
}
