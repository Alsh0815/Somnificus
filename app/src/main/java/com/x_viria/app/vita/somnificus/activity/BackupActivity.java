package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        SPStorage sps = new SPStorage(this);

        long lastBackupDate = sps.getLong(Config.KEY__LAST_BACKUP_DATE, SPDefault.LAST_BACKUP_DATE);
        if (0 < lastBackupDate) {
            TextView tv_last_backup = findViewById(R.id.BackupActivity__Last_Backup_Date);
            Date date = new Date();
            date.setTime(lastBackupDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.common__text_date_format) + " E H:mm");
            tv_last_backup.setText(simpleDateFormat.format(date));
        }

        long lastBackupSize = sps.getLong(Config.KEY__LAST_BACKUP_SIZE, SPDefault.LAST_BACKUP_SIZE);
        if (0 < lastBackupSize) {
            TextView tv_last_backup_size = findViewById(R.id.BackupActivity__Last_Backup_Size);
            double lastBackupSizeFloat = ((double) lastBackupSize) / 1024.0f;
            if (lastBackupSizeFloat < 1024.0f) {
                tv_last_backup_size.setText(String.format("%.2f KB", lastBackupSizeFloat));
            } else {
                lastBackupSizeFloat = lastBackupSizeFloat / 1024.0f;
                tv_last_backup_size.setText(String.format("%.2f MB", lastBackupSizeFloat));
            }
        }
    }
}