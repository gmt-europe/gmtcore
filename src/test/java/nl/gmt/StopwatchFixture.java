package nl.gmt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class StopwatchFixture {
    @Test
    public void simpleTest() throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.startNew();

        Thread.sleep(100);

        stopwatch.stop();

        assertEquals(0, round(stopwatch.elapsedMilliseconds() - 100));
        assertEquals(0, round((int)stopwatch.elapsed().getTotalMilliseconds() - 100));
    }

    private long round(long milliseconds) {
        if (milliseconds < 3 && milliseconds > -3) {
            return 0;
        }

        return milliseconds;
    }
}
