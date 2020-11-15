package com.example.note_taking_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.note_taking_application.security.Encryption;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
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

    EditText date;

    private Calendar c;
    private Date date1;
    Timestamp ts;
    String selected_date;
    Map<String,Object> note;


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
       c=Calendar.getInstance();
        note = new HashMap<>();
        final DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document(data.getStringExtra("noteId"));
        editNoteContent = findViewById(R.id.editNoteContent);
        editNoteTitle = findViewById(R.id.editNoteTitle);
        date=findViewById(R.id.date);

        selected_date=data.getStringExtra("date");

       // Log.d("yadvendra500",selected_date);

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);
        date.setText(selected_date);
        // putting default date

        note.put("date",selected_date);



        c=Calendar.getInstance();
        ts=new Timestamp(new Date().getTime());
        // putting default timestamp

       docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                   DocumentSnapshot document=task.getResult();
                   note.put("timeStamp",document.get("timeStamp"));
               }
               else{
                   note.put("timeStamp",ts.getTime());
               }

           }
       });






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

                //DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document(data.getStringExtra("noteId"));
              //  Map<String,Object> note = new HashMap<>();

                  //docref.
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

    public void set_date(View view) {


        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //  c.

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selected_date=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        note.put("date",selected_date);
                        c.set(year,monthOfYear + 1,dayOfMonth);
                        Date date1=c.getTime();
                        ts=new Timestamp(date1.getTime());
                        note.put("timeStamp",ts.getTime());

                        date.setText(selected_date);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }
}