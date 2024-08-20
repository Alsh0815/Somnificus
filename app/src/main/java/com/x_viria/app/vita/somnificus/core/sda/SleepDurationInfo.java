package com.x_viria.app.vita.somnificus.core.sda;

public class SleepDurationInfo {

    private final long INFO__BED_TIME;
    private final long INFO__WAKEUP_TIME;

    public boolean FLAG__NAP = false;

    public int EVAL__GOOD_OR_BAD = Eval.NONE;
    public String ID = null;

    public SleepDurationInfo(long bedTime, long wakeupTime) {
        this.INFO__BED_TIME = bedTime;
        this.INFO__WAKEUP_TIME = wakeupTime;
    }

    public long getBedTime() {
        return INFO__BED_TIME;
    }

    public long getWakeupTime() {
        return INFO__WAKEUP_TIME;
    }

    public static class Eval {

        public static final int NONE = 0x00000000;
        public static final int BAD = 0x10000000;
        public static final int GOOD = 0x10000001;

    }

}
