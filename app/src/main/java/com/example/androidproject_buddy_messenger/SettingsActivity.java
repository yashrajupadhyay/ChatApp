package com.example.androidproject_buddy_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject_buddy_messenger.Models.Users;
import com.example.androidproject_buddy_messenger.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                binding.etstatus.setText(users.getStatus());
                                binding.txtusername.setText(users.getUserName());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!binding.etstatus.getText().toString().equals("")&&!binding.txtusername.getText().toString().equals(""))
                {
                    String status = binding.etstatus.getText().toString();
                    String username = binding.txtusername.getText().toString();

                    HashMap<String , Object>obj = new HashMap<>();
                    obj.put("userName",username);
                    obj.put("status",status);

                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .updateChildren(obj);

                    Toast.makeText(SettingsActivity.this, "Profile Update ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Please Enter username and Status", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
