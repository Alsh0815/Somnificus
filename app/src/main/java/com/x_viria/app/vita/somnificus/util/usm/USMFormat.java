package com.x_viria.app.vita.somnificus.util.usm;

public class USMFormat {

    public String appName;
    public String packageName;
    public int category;
    public int eventType;
    public long timestamp;

    public USMFormat(String packageName) {
        this.appName = packageName;
        this.packageName = packageName;
    }

}
