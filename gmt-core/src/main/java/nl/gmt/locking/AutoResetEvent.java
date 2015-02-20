package nl.gmt.locking;

public class AutoResetEvent implements WaitEvent {
    private final Object monitor = new Object();
    private volatile boolean open;

    public AutoResetEvent(boolean open) {
        this.open = open;
    }

    @Override
    public void waitOne() throws InterruptedException {
        synchronized (monitor) {
            while (!open) {
                monitor.wait();
            }

            open = false;
        }
    }

    @Override
    public boolean waitOne(long timeout) throws InterruptedException {
        if (timeout < 0) {
            waitOne();
            return true;
        }

        if (timeout == 0) {
            synchronized (monitor) {
                if (open) {
                    open = false;
                    return true;
                }

                return false;
            }
        }

        synchronized (monitor) {
            long end = System.currentTimeMillis() + timeout;
            boolean result = true;

            while (!open) {
                long remaining = end - System.currentTimeMillis();

                if (remaining <= 0) {
                    result = open;
                    break;
                }

                monitor.wait(remaining);
            }

            open = false;

            return result;
        }
    }

    @Override
    public void set() {
        synchronized (monitor) {
            open = true;

            monitor.notify();
        }
    }

    @Override
    public void reset() {
        open = false;
    }
}
