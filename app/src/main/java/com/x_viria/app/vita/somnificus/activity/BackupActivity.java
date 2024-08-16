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

        long lastBackupDate = new SPStorage(this).getLong(Config.KEY__LAST_BACKUP_DATE, SPDefault.LAST_BACKUP_DATE);
        TextView tv_last_backup = findViewById(R.id.BackupActivity__Last_Backup);
        if (lastBackupDate < 0) {
            tv_last_backup.setText(getString(R.string.activity_backup__last_backup_failed));
        } else {
            Date date = new Date();
            date.setTime(lastBackupDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.common__text_date_format) + " E H:mm");
            tv_last_backup.setText(simpleDateFormat.format(date));
        }
    }
}