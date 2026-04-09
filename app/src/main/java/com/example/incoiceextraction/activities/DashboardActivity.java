package com.example.incoiceextraction.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.incoiceextraction.R;
import com.example.incoiceextraction.database.DatabaseHelper;
import com.example.incoiceextraction.utils.CSVExporter;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    Button scanBtn, folderBtn, exportBtn;
    Spinner folderSpinner;

    int userId;

    ArrayList<String> folderList = new ArrayList<>();
    ArrayList<Integer> folderIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 🔥 Get userId
        userId = getIntent().getIntExtra("userId", -1);

        scanBtn = findViewById(R.id.scanBtn);
        folderBtn = findViewById(R.id.folderBtn);
        exportBtn = findViewById(R.id.exportBtn);
        folderSpinner = findViewById(R.id.folderSpinner);

        loadFolders();

        // 📷 Scan
        scanBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // 📁 Folder
        folderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, FolderActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // 📊 Export CSV
        exportBtn.setOnClickListener(v -> {

            if (folderIdList.size() == 0) {
                Toast.makeText(this, "No folders available", Toast.LENGTH_SHORT).show();
                return;
            }

            int position = folderSpinner.getSelectedItemPosition();
            int folderId = folderIdList.get(position);

            CSVExporter.export(this, folderId);
        });
    }

    // 🔥 LOAD FOLDERS INTO SPINNER
    void loadFolders() {

        DatabaseHelper db = new DatabaseHelper(this);
        SQLiteDatabase database = db.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT id, name FROM folders",
                null
        );

        folderList.clear();
        folderIdList.clear();

        while (cursor.moveToNext()) {
            folderIdList.add(cursor.getInt(0));
            folderList.add(cursor.getString(1));
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                folderList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        folderSpinner.setAdapter(adapter);
    }

    // 🔄 Refresh folders when coming back from FolderActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadFolders();
    }
}