package com.example.plantify_user.orderDeliveries;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantify_user.R;


public class Delivery_product_info extends Fragment {

    String orderKey;


    public Delivery_product_info(String orderKey) {

        this.orderKey = orderKey;

    }





    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_delivery_product_info, container, false);


            return view;
        }
}