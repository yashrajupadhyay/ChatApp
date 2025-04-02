package com.example.androidproject_buddy_messenger.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.androidproject_buddy_messenger.MainActivity;
import com.example.androidproject_buddy_messenger.R;

public class StatusFragment extends Fragment {

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        // Find "Let's Chat" button
        Button chatButton = view.findViewById(R.id.chat);

        // Set onClickListener to navigate to MainActivity
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
