package com.example.androidproject_buddy_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.androidproject_buddy_messenger.Adapter.ChatAdapter;
import com.example.androidproject_buddy_messenger.Models.MessageModel;
import com.example.androidproject_buddy_messenger.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;
    ActivityChatDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avtar1).into(binding.profileImage);

        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        // Load messages from Firebase
        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey()); // Set message ID
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Send message logic
        binding.send.setOnClickListener(view -> {
            String message = binding.entermessage.getText().toString();
            if (message.isEmpty()) return; // Prevent empty messages

            final MessageModel model = new MessageModel(senderId, message);
            model.setTimestamp(new Date().getTime());

            binding.entermessage.setText("");

            // Generate a unique key for the message
            String messageId = database.getReference().child("chats").child(senderRoom).push().getKey();
            model.setMessageId(messageId); // Store the same ID

            // Save message in sender's chat
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child(messageId)
                    .setValue(model)
                    .addOnSuccessListener(unused -> {
                        // Save the same message in receiver's chat
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child(messageId) // Use the same message ID
                                .setValue(model);
                    });
        });
    }
}
