package com.x_viria.app.vita.somnificus.core.alarm;

public class AlarmTime {

    private final int H;
    private final int M;
    private final int S;

    public AlarmTime(
            int hour,
            int min,
            int sec
    ) {
        this.H = hour;
        this.M = min;
        this.S = sec;
    }

    public int getH() {
        return H;
    }

    public int getM() {
        return M;
    }

    public int getS() {
        return S;
    }

}
