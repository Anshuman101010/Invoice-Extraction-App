package com.example.incoiceextraction.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.incoiceextraction.R;
import com.example.incoiceextraction.database.DatabaseHelper;

import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity {

    EditText folderName;
    Button createFolderBtn;
    ListView folderListView;

    DatabaseHelper db;

    ArrayList<String> folderList;
    ArrayList<Integer> folderIds;

    ArrayAdapter<String> adapter;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        folderName = findViewById(R.id.folderName);
        createFolderBtn = findViewById(R.id.createFolderBtn);
        folderListView = findViewById(R.id.folderListView);

        db = new DatabaseHelper(this);

        folderList = new ArrayList<>();
        folderIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                folderList
        );

        folderListView.setAdapter(adapter);

        // 🔥 GET USER ID
        userId = getIntent().getIntExtra("userId", -1);

        loadFolders();

        // ➕ CREATE FOLDER
        createFolderBtn.setOnClickListener(v -> {

            String name = folderName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter folder name", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = db.insertFolder(name, userId);

            if (inserted) {
                Toast.makeText(this, "Folder Created", Toast.LENGTH_SHORT).show();
                folderName.setText("");
                loadFolders();
            } else {
                Toast.makeText(this, "Error creating folder", Toast.LENGTH_SHORT).show();
            }
        });

        // 📂 CLICK → VIEW DATA
        folderListView.setOnItemClickListener((parent, view, position, id) -> {

            int folderId = folderIds.get(position);

            Cursor cursor = db.getScansByFolder(folderId);

            StringBuilder data = new StringBuilder();

            while (cursor.moveToNext()) {
                data.append("Invoice: ").append(cursor.getString(0)).append("\n");
                data.append("Date: ").append(cursor.getString(1)).append("\n");
                data.append("Total: ").append(cursor.getString(2)).append("\n\n");
            }

            cursor.close();

            if (data.length() == 0) {
                data.append("No records found");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Folder Data")
                    .setMessage(data.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        // ❌ LONG CLICK → DELETE
        folderListView.setOnItemLongClickListener((parent, view, position, id) -> {

            int folderId = folderIds.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Folder")
                    .setMessage("Delete this folder and all its data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteFolder(folderId);
                        loadFolders();
                        Toast.makeText(this, "Folder Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
    }

    void loadFolders() {

        Cursor cursor = db.getAllFolders(userId);

        folderList.clear();
        folderIds.clear();

        if (cursor.moveToFirst()) {
            do {
                folderIds.add(cursor.getInt(0));
                folderList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}