package com.example.androidproject_buddy_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.androidproject_buddy_messenger.Adapter.FragmentAdapter;
import com.example.androidproject_buddy_messenger.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {



    FirebaseAuth mAuth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.viewpager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewpager );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();  // Store the ID in a variable

        if (id == R.id.settings)
        {
            //Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent2);
            return true;
        } else if (id == R.id.groupChat)
        {
            //Toast.makeText(this, "Group Chat Clicked", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(MainActivity.this,GroupChat.class);
            startActivity(intent1);
            return true;
        }
        else if (id == R.id.logout)
        {
           // Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
           mAuth.signOut();
            Intent intent = new Intent(MainActivity.this , login.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}