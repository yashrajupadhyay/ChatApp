package com.example.androidproject_buddy_messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidproject_buddy_messenger.Models.Users;
import com.example.androidproject_buddy_messenger.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {



//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
GoogleSignInClient mGoogleSignInClint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
      binding = ActivityLoginBinding.inflate(getLayoutInflater());

      setContentView(binding.getRoot());

      mAuth = FirebaseAuth.getInstance();
      firebaseDatabase = FirebaseDatabase.getInstance();
      progressDialog = new ProgressDialog(login.this);
      progressDialog.setTitle("Login");
      progressDialog.setMessage("Please Wait \nValidation in progress ");


        GoogleSignInOptions gos = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR-WEB-CLIENT-ID") // ✅ Correct
                .requestEmail()
                .build();


        mGoogleSignInClint =GoogleSignIn.getClient(this,gos);

        binding.btnsignin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              if(!binding.txtemail.getText().toString().isEmpty()&& !binding.txtpass.getText().toString().isEmpty())
              {
                  progressDialog.show();
                  mAuth.signInWithEmailAndPassword(binding.txtemail.getText().toString(),binding.txtpass.getText().toString())
                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                              }
                          });
              }
              else
              {
                  Toast.makeText(login.this, "Enter Credentials ", Toast.LENGTH_SHORT).show();
              }

          }
      });

      if(mAuth.getCurrentUser()!=null)
      {
          Intent intent = new Intent(login.this , MainActivity.class);
          startActivity(intent);
      }
      binding.txtdonthaveacc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(login.this , Signup.class);
              startActivity(intent);
          }
      });

      binding.btngoogle.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              SignIn();
          }
      });

    }

    int RC_SIGN_IN = 65;
    private  void SignIn()
    {
        Intent signInIntent = mGoogleSignInClint.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public  void onActivityResult(int requestCode , int resultCode , Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN)
        {
            Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG","firebaseAuthWithGoogle:"+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            }
            catch (ApiException e)
            {
                Log.w("TAG","Google Sign in Fail ",e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        Users users = new Users();

                        users.setUserId(user.getUid());

                        users.setUserName(user.getDisplayName());
                        users.setProfilePic(user.getPhotoUrl().toString());

                        firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(users);
                        Log.d("TAG", "User Email: " + user.getEmail()); // Check if user is retrieved

                        Intent intent = new Intent(login.this , MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "Signed in with Google", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}