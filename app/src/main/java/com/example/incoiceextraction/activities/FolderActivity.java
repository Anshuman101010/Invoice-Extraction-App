package com.example.incoiceextraction.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

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
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        folderName = findViewById(R.id.folderName);
        createFolderBtn = findViewById(R.id.createFolderBtn);
        folderListView = findViewById(R.id.folderListView);

        db = new DatabaseHelper(this);

        folderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                folderList);

        folderListView.setAdapter(adapter);

        loadFolders(); // 🔥 load existing folders

        createFolderBtn.setOnClickListener(v -> {

            String name = folderName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter folder name", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = db.insertFolder(name);

            if (inserted) {
                Toast.makeText(this, "Folder Created", Toast.LENGTH_SHORT).show();
                folderName.setText("");

                loadFolders(); // 🔥 REFRESH LIST
            } else {
                Toast.makeText(this, "Error creating folder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 LOAD FOLDERS FROM DB
    void loadFolders() {

        Cursor cursor = db.getAllFolders();

        folderList.clear();

        if (cursor.moveToFirst()) {
            do {
                folderList.add(cursor.getString(1)); // folder name
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
    }
}