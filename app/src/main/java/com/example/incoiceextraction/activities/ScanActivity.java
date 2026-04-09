package com.example.incoiceextraction.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.incoiceextraction.R;
import com.example.incoiceextraction.database.DatabaseHelper;
import com.example.incoiceextraction.utils.OCRProcessor;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    Button btnCapture;
    TextView txtResult;
    Spinner folderSpinner;

    Bitmap imageBitmap;

    ArrayList<String> folderList;
    ArrayList<Integer> folderIds;

    int CAMERA_PERMISSION_CODE = 101;
    int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btnCapture = findViewById(R.id.btnCapture);
        txtResult = findViewById(R.id.txtResult);
        folderSpinner = findViewById(R.id.folderSpinner);

        folderList = new ArrayList<>();
        folderIds = new ArrayList<>();

        loadFolders();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }

        btnCapture.setOnClickListener(v -> openCamera());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    void loadFolders() {

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getAllFolders();

        folderList.clear();
        folderIds.clear();

        if (cursor.moveToFirst()) {
            do {
                folderIds.add(cursor.getInt(0));
                folderList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        if (folderList.isEmpty()) {
            folderList.add("No Folders");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                folderList
        );

        folderSpinner.setAdapter(adapter);
    }

    void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            if (data != null && data.getExtras() != null) {

                imageBitmap = (Bitmap) data.getExtras().get("data");

                OCRProcessor processor = new OCRProcessor();
                processor.processImage(imageBitmap, txtResult);

                // ✅ WAIT FOR OCR RESULT
                txtResult.postDelayed(() -> {

                    String extractedText = txtResult.getText().toString();

                    if (extractedText.isEmpty()) {
                        Toast.makeText(this, "OCR failed. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // DEFAULT VALUES
                    String invoice = "N/A";
                    String date = "N/A";
                    String total = "0";

                    // EXTRACT INVOICE
                    if (extractedText.matches("(?s).*Invoice.*")) {
                        invoice = extractedText.replaceAll("(?s).*Invoice[^0-9]*(\\d+).*", "$1");
                    }

                    // EXTRACT DATE
                    if (extractedText.matches("(?s).*\\d{2}/\\d{2}/\\d{4}.*")) {
                        date = extractedText.replaceAll("(?s).*(\\d{2}/\\d{2}/\\d{4}).*", "$1");
                    }

                    // EXTRACT TOTAL
                    if (extractedText.matches("(?s).*Total.*")) {
                        total = extractedText.replaceAll("(?s).*Total[^0-9]*(\\d+\\.?\\d*).*", "$1");
                    }

                    int position = folderSpinner.getSelectedItemPosition();

                    if (folderIds.size() == 0) {
                        Toast.makeText(this, "No folder available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int folderId = folderIds.get(position);

                    DatabaseHelper db = new DatabaseHelper(this);
                    SQLiteDatabase database = db.getWritableDatabase();

                    database.execSQL(
                            "INSERT INTO scans(invoice,date,total,folder_id) VALUES(?,?,?,?)",
                            new Object[]{invoice, date, total, folderId}
                    );

                    Toast.makeText(this, "Saved to folder", Toast.LENGTH_SHORT).show();

                }, 1500); // ⏳ delay for OCR

            } else {
                txtResult.setText("Failed to capture image");
            }
        }
    }
}