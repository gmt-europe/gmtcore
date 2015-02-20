package nl.gmt.locking;

public interface WaitEvent {
    void waitOne() throws InterruptedException;

    boolean waitOne(long milliseconds) throws InterruptedException;

    void set();

    void reset();
}
