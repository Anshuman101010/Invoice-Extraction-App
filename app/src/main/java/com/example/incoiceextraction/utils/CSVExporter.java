package com.example.incoiceextraction.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.incoiceextraction.database.DatabaseHelper;

import java.io.OutputStream;

public class CSVExporter {

    public static void export(Context context, int folderId) {

        try {

            DatabaseHelper db = new DatabaseHelper(context);
            SQLiteDatabase database = db.getReadableDatabase();

            Cursor cursor = database.rawQuery(
                    "SELECT invoice, date, total FROM scans WHERE folder_id=?",
                    new String[]{String.valueOf(folderId)}
            );

            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(context, "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            String fileName = "invoices_" + System.currentTimeMillis() + ".csv";

            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                // ✅ Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(
                        MediaStore.Files.getContentUri("external"),
                        values
                );

                if (uri == null) {
                    Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show();
                    return;
                }

                outputStream = context.getContentResolver().openOutputStream(uri);

            } else {

                // ✅ Older Android
                java.io.File file = new java.io.File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                );

                outputStream = new java.io.FileOutputStream(file);
            }

            if (outputStream == null) {
                Toast.makeText(context, "Output stream error", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder data = new StringBuilder();
            data.append("Invoice,Date,Total\n");

            while (cursor.moveToNext()) {

                String invoice = cursor.getString(0);
                String date = cursor.getString(1);
                String total = cursor.getString(2);

                data.append(invoice != null ? invoice : "").append(",");
                data.append(date != null ? date : "").append(",");
                data.append(total != null ? total : "").append("\n");
            }

            outputStream.write(data.toString().getBytes());
            outputStream.flush();
            outputStream.close();
            cursor.close();

            Toast.makeText(context, "CSV saved in Downloads ✅", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Export Failed ❌", Toast.LENGTH_SHORT).show();
        }
    }
}