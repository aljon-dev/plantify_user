package com.example.plantify_user.userSettings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.plantify_user.R;
import com.example.plantify_user.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class userSettings extends Fragment {


    public userSettings() {
        // Required empty public constructor
    }

    private EditText usernameEditText,contactEditText,addressEditText,zipcodeEditText;
    private ImageView    profileImage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String userid = firebaseAuth.getUid();

        usernameEditText = view.findViewById(R.id.usernameEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        zipcodeEditText = view.findViewById(R.id.zipcodeEditText);

        firebaseDatabase.getReference("Users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                usernameEditText.setText(userModel.getUsername());
                contactEditText.setText(userModel.getContact());
                addressEditText.setText(userModel.getAddress());
                zipcodeEditText.setText(userModel.getZipcode());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}