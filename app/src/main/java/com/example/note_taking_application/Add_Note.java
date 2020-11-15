package com.example.note_taking_application;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.note_taking_application.auth.Register;
import com.example.note_taking_application.security.Encryption;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Add_Note extends AppCompatActivity {

    FirebaseFirestore fStore;
    EditText noteTitle,noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;
    Intent data;
    private SharedPreferences mPreferences;
    private String sharedPrefFile =
            "com.example.note_taking_application";

    EditText date;

    private Calendar c;
    private Date date1;
    Timestamp ts;
    String selected_date;
    Map<String,Object> note;

    private int year, month, day;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__note);
        note = new HashMap<>();
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        data=getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        date=findViewById(R.id.date);



            // calendar

        c=Calendar.getInstance();
        ts=new Timestamp(new Date().getTime());
        int month=c.get(Calendar.MONTH)+1;

        selected_date= c.get(Calendar.DAY_OF_MONTH) +"/"+ month+"/"+ c.get(Calendar.YEAR);
        note.put("timeStamp",ts.getTime());

        note.put("date",selected_date);









         user= FirebaseAuth.getInstance().getCurrentUser();
        progressBarSave = findViewById(R.id.progressBar);
       // Log.d("yadvendra 41",data.getStringExtra("password"));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Base64.Encoder encoder = Base64.getEncoder();
                fStore = FirebaseFirestore.getInstance();
                noteContent = findViewById(R.id.edit_note_detail);
                noteTitle = findViewById(R.id.add_note_title);
                String ntitle=noteTitle.getText().toString();
                String ncontent=noteContent.getText().toString();
                String encrypted_title=" ";
                String encrypted_content=" ";
                Encryption encryption=new Encryption();

                //Log.d("yadvendra 42",data.getStringExtra("password"));
                try {
                    HashMap<String, Object> first=encryption.encrypt(mPreferences.getString("password","password"),ntitle);
                     encrypted_title= encoder.encodeToString(( byte[])first.get("ciphertext"));
                     note.put("titleSalt",encoder.encodeToString(( byte[])first.get("salt")));
                     note.put("titleiv",encoder.encodeToString(( byte[])first.get("iv")));





                    HashMap<String, Object> second=encryption.encrypt(mPreferences.getString("password","password"),ncontent);
                    encrypted_content=encoder.encodeToString(( byte[])second.get("ciphertext"));
                    note.put("contentSalt",encoder.encodeToString(( byte[])second.get("salt")));
                   note.put("contentiv",encoder.encodeToString(( byte[])second.get("iv")));

                } catch (Exception e) {

                    e.printStackTrace();
                }


                if(ntitle.isEmpty() || ncontent.isEmpty()){
                    Toast.makeText(Add_Note.this, "Can not Save note with Empty Field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarSave.setVisibility(View.VISIBLE);
                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document();



              //  Map<String,Object> note = new HashMap<>();
                note.put("title",encrypted_title);
                note.put("content",encrypted_content);
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
//        else if(item.getItemId()==R.id.search){
//            searchSetup(item);


        //}
        return true;
    }



    public void set_date(View view) {
       // final Calendar c = Calendar.getInstance();
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