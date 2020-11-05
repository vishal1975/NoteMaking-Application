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
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.note_taking_application.auth.Login;
import com.example.note_taking_application.auth.Register;
import com.example.note_taking_application.model.Note;
//import com.example.note_taking_application.Add_Note;
//import com.example.note_taking_application.EditNote;
//import com.example.note_taking_application.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView nav_view;
    RecyclerView note_list;
    FirebaseFirestore fStore;
    FirebaseAuth fauth;
    FirebaseUser user;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;


   // int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           user=FirebaseAuth.getInstance().getCurrentUser();
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
                startActivity(new Intent(MainActivity.this, Add_Note.class));
            }
        });


        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("notes").document(user.getUid()).collection("MyNotes").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull final Note note) {


                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();
                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("code", code);
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);

                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                i.putExtra("code", code);
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
                                        // note deleted
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
                        menu.show();
                    }
                });

            }
        };
                note_list.setAdapter(noteAdapter);

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
             if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
                 displayAlert();
             }
             else {
                 FirebaseAuth.getInstance().signOut();
                 Toast.makeText(this, "Sucessesfuly Signed Out", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(getApplicationContext(),MainActivity.class));
             }

            }
            public void displayAlert(){
                AlertDialog.Builder warning=new AlertDialog.Builder(this)
                        .setTitle("Are You Sure")
                        .setMessage("You are logged in With Temporary account , Logging Out will Delete your All Note")
                        .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getApplicationContext(), Register.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(getApplicationContext(),Splash.class));
                                        finish();
                                    }
                                });
                            }
                        });
                warning.show();
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.option_menu, menu);

                return super.onCreateOptionsMenu(menu);
            }

            @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.setting) {
                    Toast.makeText(this, "setting is clicked", Toast.LENGTH_LONG).show();
                }
                return super.onOptionsItemSelected(item);
            }



            public class NoteViewHolder extends RecyclerView.ViewHolder {
                TextView noteTitle, noteContent;
                View view;
                CardView mCardView;

                public NoteViewHolder(@NonNull View itemView) {
                    super(itemView);

                    noteTitle = itemView.findViewById(R.id.titles);
                    noteContent = itemView.findViewById(R.id.content);
                    mCardView = itemView.findViewById(R.id.noteCard);
                    view = itemView;
                }
            }


            private int getRandomColor() {

                List<Integer> colorCode = new ArrayList<>();
                colorCode.add(R.color.blue);
                colorCode.add(R.color.yellow);
                colorCode.add(R.color.skyblue);
                colorCode.add(R.color.lightPurple);
                colorCode.add(R.color.lightGreen);
                colorCode.add(R.color.gray);
                colorCode.add(R.color.pink);
                colorCode.add(R.color.red);
                colorCode.add(R.color.greenlight);
                colorCode.add(R.color.notgreen);

                Random randomColor = new Random();
                int number = randomColor.nextInt(colorCode.size());
                return colorCode.get(number);

            }


            @Override
            protected void onStart() {
                super.onStart();
                noteAdapter.startListening();
            }

            @Override
            protected void onStop() {
                super.onStop();
                if (noteAdapter != null) {
                    noteAdapter.stopListening();
                }
            }


        }
