package com.x_viria.app.vita.somnificus.core;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.io.IOException;

public class SomnificusBackupAgent extends BackupAgentHelper {

    public static final String BACKUP_KEY__SHARED_PREF = "SOMNIFICUS_BACKUP_KEY__SHARED_PREF";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesBackupHelper sharedPreferencesBackupHelper = new SharedPreferencesBackupHelper(this, "X-VIRIA_SOMNIFICUS_SHARED_PREF");
        addHelper(BACKUP_KEY__SHARED_PREF, sharedPreferencesBackupHelper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        SPStorage sps = new SPStorage(this);
        sps.setLong(Config.KEY__LAST_BACKUP_DATE, System.currentTimeMillis());
        super.onBackup(oldState, data, newState);
    }
}