package nl.gmt.locking;

public class LeveledLockViolationException extends RuntimeException {
    public LeveledLockViolationException() {
    }

    public LeveledLockViolationException(String s) {
        super(s);
    }

    public LeveledLockViolationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public LeveledLockViolationException(Throwable throwable) {
        super(throwable);
    }

    public LeveledLockViolationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
