package com.example.plantify_user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class checkouts extends Fragment {


    public checkouts(){

    }

    private RecyclerView ListCartView;
    private ArrayList<CheckOutModel> CheckList;

    private CheckOutAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkouts, container, false);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        String userid = firebaseAuth.getCurrentUser().getUid();

        ListCartView = view.findViewById(R.id.ListCartView);

        ListCartView.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckList = new ArrayList<>();
        adapter = new CheckOutAdapter(getContext(),CheckList);
        ListCartView.setAdapter(adapter);


        firebaseDatabase.getReference("For_CheckOut").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    CheckOutModel checkOutModel = ds.getValue(CheckOutModel.class);
                    if(checkOutModel .getUserid().equals(userid)) {
                        CheckList.add(checkOutModel );
                        checkOutModel .setCartKey(ds.getKey());
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return  view;
    }
}