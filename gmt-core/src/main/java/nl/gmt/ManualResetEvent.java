// Taken from http://stackoverflow.com/questions/3625967.

package nl.gmt;

import org.apache.commons.lang.Validate;

public class ManualResetEvent {
    private final Object monitor = new Object();
    private volatile boolean open;

    public ManualResetEvent(boolean open) {
        this.open = open;
    }

    public boolean waitOne() throws InterruptedException {
        return waitOne(0);
    }

    public boolean waitOne(long timeout) throws InterruptedException {
        Validate.isTrue(timeout >= 0, "Timeout must be greater than or equal to zero");

        synchronized (monitor) {
            while (!open) {
                monitor.wait(timeout);

                // This is wrong. We don't "know" we exited because of a timeout. However, this is as close as we
                // can get and if we don't have multiple threads setting the event, this is correct.

                if (timeout > 0) {
                    return open;
                }
            }
        }

        return true;
    }

    public boolean waitOne(Interval timeout) throws InterruptedException {
        Validate.notNull(timeout, "timeout");

        return waitOne((long)timeout.getTotalMilliseconds());
    }

    public void set() {
        synchronized (monitor) {
            open = true;
            monitor.notifyAll();
        }
    }

    public void reset() {
        synchronized (monitor) {
            open = false;
        }
    }
}
