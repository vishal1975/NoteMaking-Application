package com.example.note_taking_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.note_taking_application.auth.Login;
import com.example.note_taking_application.auth.Register;
import com.example.note_taking_application.model.FirestoreAdapter;
import com.example.note_taking_application.model.Note;
//import com.example.note_taking_application.Add_Note;
//import com.example.note_taking_application.EditNote;
//import com.example.note_taking_application.NoteDetails;
import com.example.note_taking_application.security.Decryption;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView nav_view;
    RecyclerView note_list;
    FirebaseFirestore fStore;
    FirebaseAuth fauth;
    FirebaseUser user;
    //FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    Intent data;

    FirestoreAdapter noteAdapter1;

    private SharedPreferences mPreferences;
    private String sharedPrefFile =
            "com.example.note_taking_application";

    // int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = getIntent();
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        note_list = findViewById(R.id.note_list);


        note_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        FloatingActionButton fabmain = findViewById(R.id.fabmain);

        fabmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add_Note.class);
                intent.putExtra("password", data.getStringExtra("password"));
                startActivity(intent);
            }
        });


        fStore = FirebaseFirestore.getInstance();





         Query query = fStore.collection("notes").document(user.getUid()).collection("MyNotes").orderBy("title", Query.Direction.DESCENDING);



                            FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                                    .setQuery(query, Note.class)
                                    .build();
                           // final Decryption decryption = new Decryption();














                            noteAdapter1=new FirestoreAdapter(allNotes,mPreferences);
                            noteAdapter1.setOnclickListner(new FirestoreAdapter.OnItemClickListner() {
                                @Override
                                public void OnItemClick(DocumentSnapshot documentSnapshot, int position, final String decypted_title, final String decrypted_content,View v) {

                                    final Note note=documentSnapshot.toObject(Note.class);
                                    final String docId = documentSnapshot.getId();


                                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                                    menu.setGravity(Gravity.END);
                                    menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            Intent i = new Intent(getApplicationContext(), EditNote.class);
                                            i.putExtra("title",decypted_title );
                                            i.putExtra("content",decrypted_content);
                                            i.putExtra("noteId", docId);

                                            startActivity(i);
                                            return true;
                                        }
                                    });






                                    menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("MyNotes").document(docId);
                                            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(MainActivity.this, "Your Note has been deleted Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Error in Deleting Note.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            return true;


                                        }
                                    });



                                    menu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {

                                            share(decypted_title,decrypted_content);
                                            return true;
                                        }
                                    });

                                    menu.show();









                                }
                            });











                            note_list.setAdapter(noteAdapter1);





    }




    private void share(String title,String content) {
        Intent intent=new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,title);
        intent.putExtra(Intent.EXTRA_TEXT,content);
        startActivity(Intent.createChooser(intent,"Choose From The Given Application"));

    }


    @Override
            public void onBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else
                    super.onBackPressed();
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.add_note:
                        startActivity(new Intent(this, Add_Note.class));
                        break;
                    case R.id.logout:
                          checkuser();
                          break;
                    case R.id.sync_note:
                        if(user.isAnonymous()) {
                            startActivity(new Intent(this, Login.class));
                        }
                        else{
                            Toast.makeText(this, "You Already have Account", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        Toast.makeText(this, "coming soon", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            public void checkuser(){

                 FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.clear();
                preferencesEditor.apply();
                 Toast.makeText(this, "Sucessesfuly Signed Out", Toast.LENGTH_SHORT).show();
                 finish();


            }


            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.option_menu,menu);

                return true;
            }

            @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.search) {
                    //Toast.makeText(this, "setting is clicked", Toast.LENGTH_LONG).show();
                    searchSetup(item);
                    return true;
                }
                return super.onOptionsItemSelected(item);
            }



    public void searchSetup(MenuItem item){
        SearchView search= (SearchView) item.getActionView();
        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Search by title");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Query query;
                if(s.isEmpty()) {
                     query = fStore.collection("notes").document(user.getUid()).collection("MyNotes").orderBy("title", Query.Direction.DESCENDING);



                }
                else{
                    query = fStore.collection("notes").document(user.getUid()).collection("MyNotes").whereEqualTo("title",s).orderBy("title", Query.Direction.DESCENDING);
                }
                FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();

                      noteAdapter1.updateOptions(allNotes);
                return true;
            }
        });


    }




            @Override
            protected void onStart() {
                super.onStart();
                noteAdapter1.startListening();

            }

            @Override
            protected void onStop() {
                super.onStop();
                if (noteAdapter1 != null) {
                    noteAdapter1.stopListening();
                }
            }


        }
