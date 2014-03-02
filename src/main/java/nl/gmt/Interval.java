package nl.gmt;

public class Interval {
    private static final long TICKS_PER_MS = 1000000;
    private static final int MS_PER_SECOND = 1000;
    private static final int SECOND_PER_MINUTE = 60;
    private static final int MINUTE_PER_HOUR = 60;
    private static final int HOUR_PER_DAY = 24;

    private long ticks;

    public Interval(long ticks) {
        this.ticks = ticks;
    }

    public Interval(int hours, int minutes, int seconds) {
        this(0, hours, minutes, seconds);
    }

    public Interval(int days, int hours, int minutes, int seconds) {
        this(days, hours, minutes, seconds, 0);
    }

    public Interval(int days, int hours, int minutes, int seconds, int milliseconds) {
        ticks = (
            (
                (
                    (
                        (
                            days * HOUR_PER_DAY +
                            hours
                        ) * MINUTE_PER_HOUR +
                        minutes
                    ) * SECOND_PER_MINUTE +
                    seconds
                ) * MS_PER_SECOND) +
            milliseconds
        ) * TICKS_PER_MS;
    }

    public long getTicks() {
        return ticks;
    }

    public int getMilliseconds() {
        return (int)((ticks / TICKS_PER_MS) % MS_PER_SECOND);
    }

    public int getSeconds() {
        return (int)((ticks / (TICKS_PER_MS * MS_PER_SECOND)) % SECOND_PER_MINUTE);
    }

    public int getMinutes() {
        return (int)((ticks / (TICKS_PER_MS * MS_PER_SECOND * SECOND_PER_MINUTE)) % MINUTE_PER_HOUR);
    }

    public int getHours() {
        return (int)((ticks / (TICKS_PER_MS * MS_PER_SECOND * SECOND_PER_MINUTE * MINUTE_PER_HOUR)) % HOUR_PER_DAY);
    }

    public int getDays() {
        return (int)(ticks / (TICKS_PER_MS * MS_PER_SECOND * SECOND_PER_MINUTE * MINUTE_PER_HOUR * HOUR_PER_DAY));
    }

    public double getTotalMilliseconds() {
        return (double)ticks / TICKS_PER_MS;
    }

    public double getTotalSeconds() {
        return getTotalMilliseconds() / MS_PER_SECOND;
    }

    public double getTotalMinutes() {
        return getTotalSeconds() / SECOND_PER_MINUTE;
    }

    public double getTotalHours() {
        return getTotalMinutes() / MINUTE_PER_HOUR;
    }

    public double getTotalDays() {
        return getTotalHours() / HOUR_PER_DAY;
    }
}
