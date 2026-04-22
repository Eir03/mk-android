package com.example.notenote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private Button buttonProfile;
    private Button buttonSettings;
    private Button buttonStatistics;
    private Button buttonGallery;
    private Button buttonAdd;
    private Button buttonSchedule;
    private Button buttonTasks;
    private NoteAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<Note> noteList;

    private ActivityResultLauncher<Intent> addNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == NoteEditorActivity.RESULT_NOTE_SAVED) {
                        loadNotes();
                    }
                }
            });

    private ActivityResultLauncher<Intent> viewNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == NoteEditorActivity.RESULT_NOTE_SAVED ||
                            result.getResultCode() == NoteDetailsActivity.RESULT_NOTE_DELETED) {
                        loadNotes();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        noteList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        buttonProfile = findViewById(R.id.button_profile);
        buttonSettings = findViewById(R.id.button_settings);
        buttonStatistics = findViewById(R.id.button_statistics);
        buttonGallery = findViewById(R.id.button_gallery);
        buttonAdd = findViewById(R.id.button_add);
        buttonSchedule = findViewById(R.id.button_schedule);
        buttonTasks = findViewById(R.id.button_tasks);
        emptyView = findViewById(R.id.empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this, noteList, this);
        recyclerView.setAdapter(adapter);

        loadNotes();

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        buttonStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        buttonGallery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(intent);
        });

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
            addNoteLauncher.launch(intent);
        });

        buttonSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        buttonTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TasksActivity.class);
            startActivity(intent);
        });
    }

    private void loadNotes() {
        noteList = databaseHelper.getAllNotes();
        adapter.updateData(noteList);
        
        if (noteList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNoteClick(int position) {
        Note selectedNote = noteList.get(position);
        Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
        intent.putExtra(NoteDetailsActivity.EXTRA_NOTE_ID, selectedNote.getId());
        viewNoteLauncher.launch(intent);
    }
}