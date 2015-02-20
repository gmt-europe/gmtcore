package nl.gmt.locking;

class HeldLock {
    private static final ThreadLocal<HeldLock> HELD = new ThreadLocal<>();

    public static LeveledLock current() {
        HeldLock held = HELD.get();
        return held == null ? null : held.lock;
    }

    public static void verifyLock(LeveledLock lock) {
        HeldLock held = HELD.get();
        if (held == null) {
            return;
        }

        if (held.lock.getLevel() == lock.getLevel()) {
            if (!lock.getLevel().isReentrant()) {
                throw new LeveledLockViolationException("Lock is not reentrant");
            }
        } else if (held.lock.getLevel().getLevel() >= lock.getLevel().getLevel()) {
            throw new LeveledLockViolationException(String.format(
                "Cannot lock of level %d because a lock of level %d is already held",
                lock.getLevel().getLevel(),
                held.lock.getLevel().getLevel()
            ));
        }
    }

    public static void push(LeveledLock lock) {
        HELD.set(new HeldLock(lock, HELD.get()));
    }

    public static void pop() {
        HELD.set(HELD.get().next);
    }

    public static void verifyUnlock(LeveledLock lock) {
        HeldLock held = HELD.get();
        if (held == null || held.lock != lock) {
            throw new LeveledLockViolationException("Unbalanced lock/unlock pair");
        }
    }

    private final LeveledLock lock;
    private final HeldLock next;

    private HeldLock(LeveledLock lock, HeldLock next) {
        this.lock = lock;
        this.next = next;
    }
}
