package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.security.cert.PKIXRevocationChecker;

import android.text.format.DateFormat;

public class MainActivity2 extends AppCompatActivity {
    private static int sigin_in_code = 1;
    private RelativeLayout activiti_main;
    private FloatingActionButton sendBtn;
    private FirebaseListAdapter<Message> adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == sigin_in_code) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activiti_main, "Вы авторизована", Snackbar.LENGTH_LONG).show();
                displayAllMesseges();
            } else {
                Snackbar.make(activiti_main, "Вы NE авторизована", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        activiti_main = findViewById(R.id.activity_main);
        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.messageField);
                if (textField.getText().toString().equals("")) {
                    return;
                }
                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                textField.getText().toString())
                );
                textField.setText("");
            }
        });
        //np sigin
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), sigin_in_code);
        } else {
            Snackbar.make(activiti_main, "Вы авторизована", Snackbar.LENGTH_LONG).show();
            displayAllMesseges();
        }
    }

    //    private void displayAllMesseges() {
//        ListView listOfMessages = findViewById(R.id.list_of_messages);
//
//        adapter = new FirebaseListAdapter<Message>(
//                this,
//                Message.class, R.layout.list_item,
//                FirebaseDatabase.getInstance().getReference()) {
//            @Override
//            protected void populateView(View v, Message model, int position) {
//                TextView mess_user, mess_text, mess_time;
//                mess_user = v.findViewById(R.id.message_user);
//                mess_time = v.findViewById(R.id.message_time);
//                mess_text = v.findViewById(R.id.message_text);
//
//                mess_user.setText(model.getUserName());
//                mess_text.setText(model.getTextMessage());
//                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));
//            }
//        };
//        listOfMessages.setAdapter(adapter);
//
//    }
    private void displayAllMesseges() {
        @SuppressLint("WrongViewCast") RecyclerView listOfMessages = findViewById(R.id.list_of_messages);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(databaseReference, Message.class)
                        .build();

        FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                        return new RecyclerView.ViewHolder(view) {
                        };
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message model) {
                        TextView messUser, messText, messTime;
                        messUser = holder.itemView.findViewById(R.id.message_user);
                        messTime = holder.itemView.findViewById(R.id.message_time);
                        messText = holder.itemView.findViewById(R.id.message_text);

                        messUser.setText(model.getUserName());
                        messText.setText(model.getTextMessage());
                        messTime.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
                    }
                };

        listOfMessages.setLayoutManager(new LinearLayoutManager(this));
        listOfMessages.setAdapter(adapter);

        // Start listening for changes in the database
        adapter.startListening();
    }

}
