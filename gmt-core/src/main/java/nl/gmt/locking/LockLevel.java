package nl.gmt.locking;

public interface LockLevel {
    int getLevel();

    boolean isReentrant();
}
