package org.valleycollege.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    RecyclerViewAdapter adapter;
          RecyclerView recyclerView;
          ArrayList<Message> list;
          TextInputLayout message;
          DatabaseReference db;
          FloatingActionButton send;
          FirebaseAuth auth;
          FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.fab_send);
        message = findViewById(R.id.message);
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        user = auth.getCurrentUser();
        String uId = user.getUid();
        String uEmail = user.getEmail();
        String timeStamp = new SimpleDateFormat("dd-MM-yy HH:mm a").format(Calendar.getInstance().getTime());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getEditText().getText().toString();
             db.child("Messages").push().setValue(new Message(uEmail,msg,timeStamp)).addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void unused) {
                  message.getEditText().setText("");
                 }
             });
            }
        });

        adapter = new RecyclerViewAdapter(this,list);
        LinearLayoutManager llm = new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart(){
        super.onStart();
        receiveMessages();
    }

    private void receiveMessages(){
      db.child("Messages").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
             for (DataSnapshot snap:snapshot.getChildren()) {
                  Message message = snap.getValue(Message.class);
                  adapter.addMessage(message);
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
}