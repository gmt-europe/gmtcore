package nl.gmt.locking;

public class ManualResetEvent implements WaitEvent {
    private final Object monitor = new Object();
    private volatile boolean open;

    public ManualResetEvent(boolean open) {
        this.open = open;
    }

    @Override
    public void waitOne() throws InterruptedException {
        synchronized (monitor) {
            while (!open) {
                monitor.wait();
            }
        }
    }

    @Override
    public boolean waitOne(long milliseconds) throws InterruptedException {
        synchronized (monitor) {
            if (open) {
                return true;
            }

            monitor.wait(milliseconds);

            return open;
        }
    }

    @Override
    public void set() {
        synchronized (monitor) {
            open = true;
            monitor.notifyAll();
        }
    }

    @Override
    public void reset() {
        open = false;
    }
}
