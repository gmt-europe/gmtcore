package nl.gmt.locking;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LeveledLockFixture {
    @Test
    public void validLevel() {
        LeveledLock level1 = new LeveledLock(Levels.LEVEL1);
        LeveledLock level2 = new LeveledLock(Levels.LEVEL2);

        level1.lock();
        try {
            level2.lock();
            try {
                Assert.assertTrue(true);
            } finally {
                level2.unlock();
            }
        } finally {
            level1.unlock();
        }
    }

    @Test(expected = LeveledLockViolationException.class)
    public void invalidReentrant() {
        LeveledLock lock = new LeveledLock(Levels.LEVEL1);

        lock.lock();
        try {
            lock.lock();
            try {
                Assert.fail();
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void validReentrant() {
        LeveledLock lock = new LeveledLock(Levels.LEVEL2_REENTRANT);

        lock.lock();
        try {
            lock.lock();
            try {
                Assert.assertTrue(true);
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    @Test(expected = LeveledLockViolationException.class)
    public void invalidLevel() {
        LeveledLock level1 = new LeveledLock(Levels.LEVEL1);
        LeveledLock level2 = new LeveledLock(Levels.LEVEL2);

        level2.lock();
        try {
            level1.lock();
            try {
                Assert.assertTrue(true);
            } finally {
                level1.unlock();
            }
        } finally {
            level2.unlock();
        }
    }

    @Test
    public void validReadWrite() {
        LeveledReadWriteLock lock = new LeveledReadWriteLock(Levels.LEVEL1);

        lock.readLock();
        try {
            Assert.assertTrue(true);
        } finally {
            lock.readUnlock();
        }

        lock.writeLock();
        try {
            Assert.assertTrue(true);
        } finally {
            lock.writeUnlock();
        }
    }

    @Test
    public void validUpgrade() {
        LeveledReadWriteLock lock = new LeveledReadWriteLock(Levels.LEVEL1);

        lock.readLock();
        try {
            lock.upgrade();
            try {
                Assert.assertTrue(true);
            } finally {
                lock.downgrade();
            }
        } finally {
            lock.readUnlock();
        }
    }

    @Test
    public void invalidUpgradeWithoutRead() {
        LeveledReadWriteLock lock = new LeveledReadWriteLock(Levels.LEVEL1);

        lock.upgrade();
        try {
            Assert.fail();
        } finally {
            lock.downgrade();
        }
    }

    @Test
    public void invalidUpgradeWithWrite() {
        LeveledReadWriteLock lock = new LeveledReadWriteLock(Levels.LEVEL1);

        lock.writeLock();
        try {
            lock.upgrade();
            try {
                Assert.assertTrue(true);
            } finally {
                lock.downgrade();
            }
        } finally {
            lock.writeLock();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void reentrantReadWrite() {
        new LeveledReadWriteLock(Levels.LEVEL2_REENTRANT);
    }
}
