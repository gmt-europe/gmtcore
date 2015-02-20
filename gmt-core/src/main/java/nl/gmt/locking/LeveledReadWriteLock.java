package nl.gmt.locking;

import org.apache.commons.lang.Validate;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeveledReadWriteLock {
    private final LeveledLock readLock;
    private final LeveledLock writeLock;
    private final ReentrantReadWriteLock lock;

    public LeveledReadWriteLock(LockLevel level) {
        Validate.notNull(level, "level");

        if (level.isReentrant()) {
            throw new IllegalArgumentException("Read/write locks cannot be reentrant");
        }

        lock = new ReentrantReadWriteLock();
        readLock = new LeveledLock(level, lock.readLock());
        writeLock = new LeveledLock(level, lock.writeLock());
    }

    public void readLock() {
        readLock.lock();
    }

    public void readUnlock() {
        readLock.unlock();
    }

    public void writeLock() {
        if (HeldLock.current() == readLock) {
            throw new IllegalStateException("Use upgrade to upgrade a read lock to a write lock");
        }
        writeLock.lock();
    }

    public void writeUnlock() {
        writeLock.unlock();
    }

    public void upgrade() {
        if (HeldLock.current() != readLock) {
            throw new LeveledLockViolationException("Read lock must be held to upgrade the read/write lock");
        }

        HeldLock.pop();
        lock.readLock().unlock();
        lock.writeLock().lock();
        HeldLock.push(writeLock);
    }

    public void downgrade() {
        if (HeldLock.current() != writeLock) {
            throw new LeveledLockViolationException("Write lock must be held to downgrade the read/write lock");
        }

        HeldLock.pop();
        lock.readLock().lock();
        lock.writeLock().unlock();;
        HeldLock.push(readLock);
    }
}
