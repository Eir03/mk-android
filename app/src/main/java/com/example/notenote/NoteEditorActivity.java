package com.example.notenote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "note_id";
    public static final int RESULT_NOTE_SAVED = 101;
    public static final int RESULT_NOTE_DELETED = 102;

    private EditText editTitle;
    private EditText editContent;
    private Button buttonSave;
    private Button buttonCancel;

    private DatabaseHelper databaseHelper;
    private Note currentNote;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        databaseHelper = new DatabaseHelper(this);

        editTitle = findViewById(R.id.edit_note_title);
        editContent = findViewById(R.id.edit_note_content);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        // Check if we're editing an existing note
        if (getIntent().hasExtra(EXTRA_NOTE_ID)) {
            isEditMode = true;
            long noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
            if (noteId != -1) {
                currentNote = databaseHelper.getNote(noteId);
                populateFields();
            }
        } else {
            // New note
            currentNote = new Note();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void populateFields() {
        if (currentNote != null) {
            editTitle.setText(currentNote.getTitle());
            editContent.setText(currentNote.getContent());
        }
    }

    private void saveNote() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Пожалуйста введите заголовок", Toast.LENGTH_SHORT).show();
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);

        if (isEditMode) {
            databaseHelper.updateNote(currentNote);
            Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show();
        } else {
            long id = databaseHelper.addNote(currentNote);
            currentNote.setId(id);
            Toast.makeText(this, "Заметка сохранена", Toast.LENGTH_SHORT).show();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE_ID, currentNote.getId());
        setResult(RESULT_NOTE_SAVED, resultIntent);
        finish();
    }
} 