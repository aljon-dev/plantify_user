package com.example.plantify_user.carts_checkout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantify_user.R;
import com.example.plantify_user.adapter.CheckOutAdapter;
import com.example.plantify_user.model.CheckOutModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class checkouts extends Fragment {


    public checkouts(){

    }

    private RecyclerView ListCartView;
    private ArrayList<CheckOutModel> CheckList;

    private CheckOutAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private TextView totalprices;
    private MaterialButton checkOutBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.from(container.getContext())
                .inflate(R.layout.fragment_checkouts, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userid = firebaseAuth.getCurrentUser().getUid();

        ListCartView = view.findViewById(R.id.ListCartView);
        totalprices = view.findViewById(R.id.TotalPrice);
        checkOutBtn = view.findViewById(R.id.checkOutBtn);


        ListCartView.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckList = new ArrayList<>();
        adapter = new CheckOutAdapter(getContext(), CheckList);
        ListCartView.setAdapter(adapter);


        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));


        checkOutBtn.setOnClickListener(v->{
            OrderItems(userid);
        } );



        firebaseDatabase.getReference("For_CheckOut")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CheckList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CheckOutModel checkOutModel = ds.getValue(CheckOutModel.class);
                            if (checkOutModel.getUserid().equals(userid)) {
                                checkOutModel.setCartKey(ds.getKey());

                                CheckList.add(checkOutModel);
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return view;
    }

    private void OrderItems(String userid){

        String referenceId = String.valueOf(System.currentTimeMillis());

        List<Map<String, Object>> orderedItems = new ArrayList<>();
        for(CheckOutModel item:CheckList){
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("ProductKey", item.getProductKey());
            orderItem.put("ProductName", item.getProductName());
            orderItem.put("Quantity", item.getQuantity());
            orderItem.put("Price", item.getPrice());
            orderItem.put("ImageUrl", item.getImageUrl());
            orderItem.put("ProductDescription",item.getProductDescription());
            orderedItems.add(orderItem);
            firebaseDatabase.getReference("Orders").child(item.getCartKey()).removeValue();
        }


        Map<String,Object> orderInfo = new HashMap<>();
        orderInfo.put("userid",userid);
        orderInfo.put("status","For Review");

        firebaseDatabase.getReference("Orders").child(referenceId).setValue( orderedItems).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseDatabase.getReference("Orders").child(referenceId).updateChildren(orderInfo);
                }else{
                    Toast.makeText(getContext(), "Failed to Initialize", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalBill = intent.getIntExtra("totalAmount", 0);
            totalprices.setText("PHP " + totalBill); // Added currency symbol
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(mMessageReceiver);
    }
}