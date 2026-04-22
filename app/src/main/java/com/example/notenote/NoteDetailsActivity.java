package com.example.notenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "note_id";
    public static final int RESULT_NOTE_DELETED = 102;

    private TextView textTitle;
    private TextView textContent;
    private TextView textDate;
    private Button buttonEdit;
    private Button buttonDelete;

    private DatabaseHelper databaseHelper;
    private Note currentNote;
    private long noteId;

    private ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == NoteEditorActivity.RESULT_NOTE_SAVED) {
                        loadNoteDetails();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        databaseHelper = new DatabaseHelper(this);

        textTitle = findViewById(R.id.text_note_title);
        textContent = findViewById(R.id.text_note_content);
        textDate = findViewById(R.id.text_note_date);
        buttonEdit = findViewById(R.id.button_edit);
        buttonDelete = findViewById(R.id.button_delete);

        if (getIntent().hasExtra(EXTRA_NOTE_ID)) {
            noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
            if (noteId != -1) {
                loadNoteDetails();
            } else {
                Toast.makeText(this, "Ошибка: Заметка не найдена", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteDetailsActivity.this, NoteEditorActivity.class);
                intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, noteId);
                editNoteLauncher.launch(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }

    private void loadNoteDetails() {
        currentNote = databaseHelper.getNote(noteId);
        textTitle.setText(currentNote.getTitle());
        textContent.setText(currentNote.getContent());
        textDate.setText(currentNote.getFormattedDate());
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить заметку");
        builder.setMessage("Вы уверены что хотите удалить заметку?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void deleteNote() {
        databaseHelper.deleteNote(currentNote);
        Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
        setResult(RESULT_NOTE_DELETED);
        finish();
    }
} 