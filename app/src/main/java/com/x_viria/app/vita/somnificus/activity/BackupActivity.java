package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        Button BackupBtn = findViewById(R.id.BackupActivity__Make_Backup_Btn);
        BackupBtn.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            intent.putExtra(
                    Intent.EXTRA_TITLE,
                    String.format(
                            "%s.somconf",
                            sdf.format(new Date())
                    )
            );
            startActivityForResult(intent, 1);
        });

        Button RestoreBtn = findViewById(R.id.BackupActivity__Restore_Btn);
        RestoreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            startActivityForResult(intent, 2);
        });
    }

    private void restore(Map<String, Object> backupData) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.activity_backup__dialog_restore_title)
                .setMessage(R.string.activity_backup__dialog_restore_confirm_msg)
                .setNegativeButton(R.string.common__text_cancel, null)
                .setPositiveButton(R.string.common__text_ok, (dialog, which) -> {
                    SharedPreferences sharedPreferences = new SPStorage(this).get();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();

                    Map<String, ?> restoredData = (Map<String, ?>) backupData.get("DATA");
                    if (restoredData == null) {
                        Toast.makeText(this, R.string.activity_backup__toast_failed_restore, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Map.Entry<String, ?> entry : restoredData.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof String) {
                            editor.putString(entry.getKey(), (String) value);
                        } else if (value instanceof Integer) {
                            editor.putInt(entry.getKey(), (Integer) value);
                        } else if (value instanceof Boolean) {
                            editor.putBoolean(entry.getKey(), (Boolean) value);
                        } else if (value instanceof Float) {
                            editor.putFloat(entry.getKey(), (Float) value);
                        } else if (value instanceof Long) {
                            editor.putLong(entry.getKey(), (Long) value);
                        }
                    }
                    editor.apply();
                    Toast.makeText(this, R.string.activity_backup__toast_successfully_restore, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String packageName = getPackageName();
        String versionName;
        int versionCode;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (data == null) {
            if (requestCode == 1) {
                Toast.makeText(this, R.string.activity_backup__toast_failed_save, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, R.string.activity_backup__toast_failed_restore, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Uri uri = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (uri == null) {
                Toast.makeText(this, R.string.activity_backup__toast_failed_save, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream == null) {
                    Toast.makeText(this, R.string.activity_backup__toast_failed_save, Toast.LENGTH_SHORT).show();
                    return;
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                Map<String, ?> allEntries = (new SPStorage(this).get()).getAll();
                Map<String, Object> data_map = new HashMap<>();
                data_map.put("APP_VERSION_NAME", versionName);
                data_map.put("APP_VERSION_CODE", versionCode);
                data_map.put("DATA", allEntries);
                objectOutputStream.writeObject(data_map);
                objectOutputStream.close();

                Toast.makeText(this, R.string.activity_backup__toast_successfully_save, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, R.string.activity_backup__toast_failed_save, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (uri == null) {
                Toast.makeText(this, R.string.activity_backup__toast_failed_restore, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                Map<String, Object> backupData = (Map<String, Object>) objectInputStream.readObject();
                objectInputStream.close();

                int appVersionCode = (int) backupData.get("APP_VERSION_CODE");
                if (versionCode < appVersionCode) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.activity_backup__dialog_restore_title)
                            .setMessage(R.string.activity_backup__dialog_restore_newer_msg)
                            .setNegativeButton(R.string.common__text_cancel, null)
                            .setPositiveButton(R.string.common__text_ok, (dialog, which) -> restore(backupData))
                            .show();
                } else {
                    restore(backupData);
                }
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(this, R.string.activity_backup__toast_failed_restore, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}