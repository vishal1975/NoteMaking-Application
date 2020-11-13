package com.example.note_taking_application.model;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_taking_application.EditNote;
import com.example.note_taking_application.MainActivity;
import com.example.note_taking_application.NoteDetails;
import com.example.note_taking_application.R;
import com.example.note_taking_application.security.Decryption;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static androidx.core.content.ContextCompat.startActivity;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<Note, FirestoreAdapter.NoteViewHolder> {


    private SharedPreferences mPreferences;
    private String sharedPrefFile =
            "com.example.note_taking_application";
    Decryption decryption;
    OnItemClickListner listner;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<Note> options,SharedPreferences mPreferences) {

        super(options);
        this.mPreferences=mPreferences;
        decryption=new Decryption();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onBindViewHolder(@NonNull final NoteViewHolder noteViewHolder, int i, @NonNull final Note note) {
        final String docId = getSnapshots().getSnapshot(i).getId();
        noteViewHolder.noteTitle.setText(noteViewHolder.decrypted_title_body());
        noteViewHolder.noteContent.setText(noteViewHolder.decrypted_content_body());
        final int code = getRandomColor();
        noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));


        noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), NoteDetails.class);
                i.putExtra("title", noteViewHolder.decrypted_title_body());
                i.putExtra("content",noteViewHolder.decrypted_content_body());
                i.putExtra("code", code);
                i.putExtra("noteId", docId);
                v.getContext().startActivity(i);
            }
        });


    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
        return new NoteViewHolder(view);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;
        ImageView menuicon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            menuicon=itemView.findViewById(R.id.menuIcon);
            view = itemView;



             menuicon.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(final View v) {
                     int position=getAdapterPosition();


                     if(position!=RecyclerView.NO_POSITION&&listner!=null){
                         Log.d("yadvendra 100","hello");
                         listner.OnItemClick(getSnapshots().getSnapshot(position),position,decrypted_title_body(),decrypted_content_body(),v);
                     }














                 }
             });

        }





        public String decrypted_title_body(){


            int position=getAdapterPosition();
            DocumentSnapshot documentSnapshot=getSnapshots().getSnapshot(position);
            Note note=documentSnapshot.toObject(Note.class);
            String decrypted_title=note.getTitle();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    decrypted_title = decryption.decrypt(note.getTitleSalt(), note.getTitle(), note.getTitleiv(), mPreferences.getString("password", "password"));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }



            return decrypted_title;
        }



        public String decrypted_content_body(){
            int position=getAdapterPosition();
            DocumentSnapshot documentSnapshot=getSnapshots().getSnapshot(position);
            Note note=documentSnapshot.toObject(Note.class);

            String decrypted_content = note.getContent();


            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    decrypted_content = decryption.decrypt(note.getContentSalt(), note.getContent(), note.getContentiv(), mPreferences.getString("password", "password"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return decrypted_content;
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


    public interface OnItemClickListner{
        void OnItemClick(DocumentSnapshot documentSnapshot,int position,String title,String content,View v);
    }

    public void setOnclickListner(OnItemClickListner listner){
        this.listner=listner;

    }


}
