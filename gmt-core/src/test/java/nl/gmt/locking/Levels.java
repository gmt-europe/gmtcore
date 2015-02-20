package nl.gmt.locking;

public enum Levels implements LockLevel {
    LEVEL1(1, false),
    LEVEL2(2, false),
    LEVEL2_REENTRANT(2, true),
    LEVEL3(3, false);

    private final int level;
    private final boolean reentrant;

    private Levels(int level, boolean reentrant) {
        this.level = level;
        this.reentrant = reentrant;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean isReentrant() {
        return reentrant;
    }
}
