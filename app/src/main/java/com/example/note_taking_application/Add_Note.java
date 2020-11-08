package com.example.note_taking_application;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Add_Note extends AppCompatActivity {

    FirebaseFirestore fStore;
    EditText noteTitle,noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         user= FirebaseAuth.getInstance().getCurrentUser();
        progressBarSave = findViewById(R.id.progressBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore = FirebaseFirestore.getInstance();
                noteContent = findViewById(R.id.edit_note_detail);
                noteTitle = findViewById(R.id.add_note_title);
                String ntitle=noteTitle.getText().toString();
                String ncontent=noteContent.getText().toString();

                if(ntitle.isEmpty() || ncontent.isEmpty()){
                    Toast.makeText(Add_Note.this, "Can not Save note with Empty Field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarSave.setVisibility(View.VISIBLE);
                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document();



                Map<String,Object> note = new HashMap<>();
                note.put("title",ntitle);
                note.put("content",ncontent);
                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Add_Note.this, "Note Added.", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Note.this, "Error, Try again.", Toast.LENGTH_SHORT).show();
                        progressBarSave.setVisibility(View.VISIBLE);
                    }
                });




            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.close,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        else if(item.getItemId()==R.id.close){
            finish();
        }
        else if(item.getItemId()==R.id.search){
            searchSetup(item);


        }
        return true;
    }

public void searchSetup(MenuItem item){
    SearchView search= (SearchView) item.getActionView();
    search.setSubmitButtonEnabled(true);
    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
                 startActivity(new Intent(getApplicationContext(),MainActivity.class));
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {



            return true;
        }
    });


}

}