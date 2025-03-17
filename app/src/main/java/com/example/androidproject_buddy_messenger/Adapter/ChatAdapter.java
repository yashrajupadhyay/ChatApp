package com.example.androidproject_buddy_messenger.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject_buddy_messenger.Models.MessageModel;
import com.example.androidproject_buddy_messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MessageModel> messageModels;
    private Context context;
    private String receiverId;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String receiverId) {
        this.messageModels = messageModels;
        this.context = context;
        this.receiverId = receiverId;
    }

    @Override
    public int getItemViewType(int position) {
        return messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()) ? 1 : 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(formatTime(messageModel.getTimestamp()));
        } else {
            ((ReceiverViewHolder) holder).receiverMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder) holder).receiverTime.setText(formatTime(messageModel.getTimestamp()));
        }

        // Handle long click for message deletion
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteMessage(messageModel))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });
    }

    private void deleteMessage(MessageModel messageModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();

        if (messageModel.getMessageId() != null) {
            // Delete from sender's chat
            database.getReference().child("chats").child(senderRoom)
                    .child(messageModel.getMessageId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Delete from receiver's chat
                        database.getReference().child("chats").child(receiverRoom)
                                .child(messageModel.getMessageId()).removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }

    private String formatTime(long timestamp) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.reciverText);
            receiverTime = itemView.findViewById(R.id.reciverTime);
        }
    }
}
