package nl.gmt;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IntervalFixture {
    @Test
    public void simpleTests() {
        Interval interval = new Interval(1, 1, 1, 1, 1);

        assertEquals(1, interval.getDays());
        assertEquals(1, interval.getHours());
        assertEquals(1, interval.getMinutes());
        assertEquals(1, interval.getSeconds());
        assertEquals(1, interval.getMilliseconds());

        int total = 1;
        assertEquals(1, (int)interval.getTotalDays());
        total = total * 24 + 1;
        assertEquals(total, (int)interval.getTotalHours());
        total = total * 60 + 1;
        assertEquals(total, (int)interval.getTotalMinutes());
        total = total * 60 + 1;
        assertEquals(total, (int)interval.getTotalSeconds());
        total = total * 1000 + 1;
        assertEquals(total, (int)interval.getTotalMilliseconds());
    }

    @Test
    public void toStrings() {
        assertEquals("00:00:00", new Interval(0, 0, 0, 0, 0).toString());
        assertEquals("00:00:00.001000", new Interval(0, 0, 0, 0, 1).toString());
        assertEquals("00:00:01.001000", new Interval(0, 0, 0, 1, 1).toString());
        assertEquals("00:01:01.001000", new Interval(0, 0, 1, 1, 1).toString());
        assertEquals("01:01:01.001000", new Interval(0, 1, 1, 1, 1).toString());
        assertEquals("1.01:01:01.001000", new Interval(1, 1, 1, 1, 1).toString());
    }
}
