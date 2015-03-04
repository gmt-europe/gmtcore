package nl.gmt.locking;

import org.apache.commons.lang.Validate;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LeveledLock {
    private final LockLevel level;
    private final Lock lock;

    public LeveledLock(LockLevel level) {
        this(level, new ReentrantLock());
    }

    LeveledLock(LockLevel level, Lock lock) {
        Validate.notNull(level, "level");
        Validate.notNull(lock, "lock");

        this.level = level;
        this.lock = lock;
    }

    LockLevel getLevel() {
        return level;
    }

    public void lock() {
        HeldLock.verifyLock(this);
        lock.lock();
        HeldLock.push(this);
    }

    public void unlock() {
        HeldLock.verifyUnlock(this);
        HeldLock.pop();
        lock.unlock();
    }
}
