package com.roaaserver.placementstudent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.roaaserver.placementstudent.Holders.MessageViewHolder;
import com.roaaserver.placementstudent.Models.MessageClass;

public class MessagesActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;


    private ImageButton backButton;
    private RecyclerView messageRecyclerView;
    private FirestoreRecyclerAdapter firestoreMessageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initFields();

        initFirebase();


        setUpMessageRecyclerView();

        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);
        messageRecyclerView = findViewById(R.id.message_recycler_view);
        messageRecyclerView.setHasFixedSize(true);
        Context context;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        firestoreMessageRecyclerAdapter.startListening();

    }

    private void setUpMessageRecyclerView() {


        //Query
        Query query = firestore.collection("Messages").orderBy("time", Query.Direction.ASCENDING);
        //Recycler options
        FirestoreRecyclerOptions<MessageClass> options = new FirestoreRecyclerOptions.Builder<MessageClass>()
                .setQuery(query, MessageClass.class)
                .build();

        firestoreMessageRecyclerAdapter = new FirestoreRecyclerAdapter<MessageClass, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);
                return new MessageViewHolder(view, false);


            }


            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageClass model) {
                holder.setMessage(getApplication(), model.getMessage(), model.getTime(),
                        model.getSenderUid(), model.getName(), model.getImage(),model.getDepartment());


            }


            @Override
            public void onDataChanged() {
                super.onDataChanged();
             //   messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
            }
        };


        messageRecyclerView.setAdapter(firestoreMessageRecyclerAdapter);

    }
}