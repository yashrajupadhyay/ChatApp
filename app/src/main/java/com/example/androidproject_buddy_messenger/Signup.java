package com.example.androidproject_buddy_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject_buddy_messenger.Models.Users;
import com.example.androidproject_buddy_messenger.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {


    ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setTitle("Creating Account ");
        progressDialog.setMessage("we are creating your account .");

        binding.btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(!binding.txtusername.getText().toString().isEmpty() && !binding.txtemail.getText().toString().isEmpty() && !binding.txtpassword.getText().toString().isEmpty())
                 {
                     progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(binding.txtemail.getText().toString() , binding.txtpassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {
                                        Users user = new Users(binding.txtusername.getText().toString(),binding.txtemail.getText().toString(),binding.txtpassword.getText().toString());

                                        String id;
                                        id = task.getResult().getUser().getUid();
                                        database.getReference().child("Users").child(id).setValue(user);
                                        Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(Signup.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                 }
                 else
                 {
                     Toast.makeText(Signup.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                 }
            }
        });

        binding.txtAlreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this , login.class);
                startActivity(intent);
            }
        });

    }
}