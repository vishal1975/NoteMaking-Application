package com.example.note_taking_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.note_taking_application.security.Encryption;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {


    Intent data;
    EditText editNoteTitle,editNoteContent;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;
    private SharedPreferences mPreferences;
    private String sharedPrefFile =
            "com.example.note_taking_application";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        fStore = fStore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        spinner = findViewById(R.id.progressBar2);
        //user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();


        editNoteContent = findViewById(R.id.editNoteContent);
        editNoteTitle = findViewById(R.id.editNoteTitle);


        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);


        FloatingActionButton fab = findViewById(R.id.saveEditedNote);


        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String nTitle = editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();
                Base64.Encoder encoder = Base64.getEncoder();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditNote.this, "Can not Save note with Empty Field.", Toast.LENGTH_SHORT).show();
                    return;
                }
                spinner.setVisibility(View.VISIBLE);

                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document(data.getStringExtra("noteId"));
                Map<String,Object> note = new HashMap<>();


                String encrypted_title=" ";
                String encrypted_content=" ";
                Encryption encryption=new Encryption();






                try {
                    HashMap<String, Object> first=encryption.encrypt(mPreferences.getString("password","password"),nTitle);
                    encrypted_title= encoder.encodeToString(( byte[])first.get("ciphertext"));
                    note.put("titleSalt",encoder.encodeToString(( byte[])first.get("salt")));
                    note.put("titleiv",encoder.encodeToString(( byte[])first.get("iv")));





                    HashMap<String, Object> second=encryption.encrypt(mPreferences.getString("password","password"),nContent);
                    encrypted_content=encoder.encodeToString(( byte[])second.get("ciphertext"));
                    note.put("contentSalt",encoder.encodeToString(( byte[])second.get("salt")));
                    note.put("contentiv",encoder.encodeToString(( byte[])second.get("iv")));

                } catch (Exception e) {

                    e.printStackTrace();
                }









                note.put("title",encrypted_title);
                note.put("content",encrypted_content);

                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditNote.this, "Note Saved.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this, "Error, Try again.", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.VISIBLE);
                    }
                });

            }
        });





    }
}