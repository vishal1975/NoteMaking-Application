package com.example.note_taking_application.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_taking_application.MainActivity;
import com.example.note_taking_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Register extends AppCompatActivity {


    EditText mFullName,mEmail,mPassword,mconfirmPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    String password;
    private SharedPreferences mPreferences;
    private String sharedPrefFile =
            "com.example.note_taking_application";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        mFullName   = findViewById(R.id.userName);
        mEmail      = findViewById(R.id.userEmail);
        mPassword   = findViewById(R.id.password);
        mconfirmPassword    = findViewById(R.id.passwordConfirm);
        mRegisterBtn= findViewById(R.id.createAccount);
        mLoginBtn   = findViewById(R.id.login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

            getSupportActionBar().setTitle("Create Account");


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                 password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String confirmPassword   = mconfirmPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
               // AuthCredential credential= EmailAuthProvider.getCredential(email,password);
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                         if(task.isSuccessful()) {

                             SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                             preferencesEditor.putString("password", password);
                             preferencesEditor.apply();
                             Toast.makeText(Register.this, "Notes are Synced", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(getApplicationContext(), Login.class);

                             startActivity(intent);
                             finish();
                         }
                         else{
                             Toast.makeText(Register.this, "Please try Again...", Toast.LENGTH_SHORT).show();
                         }
                    }
                });
            }
        });
    }



    public String getPassword(){
        return password;
    }
}