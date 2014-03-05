package nl.gmt;

import org.apache.commons.lang.Validate;

public class Interval implements Comparable<Interval> {
    private static final long TICKS_PER_NS = 1000;
    private static final long TICKS_PER_MS = 1000000;
    private static final int MS_PER_SECOND = 1000;
    private static final int NS_PER_SECOND = 1000000;
    private static final int SECOND_PER_MINUTE = 60;
    private static final int MINUTE_PER_HOUR = 60;
    private static final int HOUR_PER_DAY = 24;

    public static Interval fromMilliseconds(double value) {
        return new Interval((long)(value * TICKS_PER_MS));
    }

    public static Interval fromSeconds(double value) {
        return fromMilliseconds(value * MS_PER_SECOND);
    }

    public static Interval fromMinutes(double value) {
        return fromSeconds(value * SECOND_PER_MINUTE);
    }

    public static Interval fromHours(double value) {
        return fromMinutes(value * MINUTE_PER_HOUR);
    }

    public static Interval fromDays(double value) {
        return fromHours(value * HOUR_PER_DAY);
    }

    private final long ticks;

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

    @Override
    public int compareTo(Interval interval) {
        Validate.notNull(interval, "interval");

        return Long.compare(ticks, interval.ticks);
    }

    @Override
    public boolean equals(Object obj) {
        return
            obj instanceof Interval &&
            ticks == ((Interval)obj).ticks;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(ticks).hashCode();
    }

    @Override
    public String toString() {
        int days = Math.abs(getDays());
        int hours = Math.abs(getHours());
        int minutes = Math.abs(getMinutes());
        int seconds = Math.abs(getSeconds());
        int nanoseconds = Math.abs((int)((ticks / TICKS_PER_NS) % NS_PER_SECOND));

        String result = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        if (nanoseconds > 0) {
            result += String.format(".%06d", nanoseconds);
        }

        if (days > 0) {
            result = days + "." + result;
        }

        if (ticks < 0) {
            result = "-" + result;
        }

        return result;
    }
}
