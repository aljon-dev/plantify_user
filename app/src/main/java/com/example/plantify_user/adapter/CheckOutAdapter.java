package com.example.plantify_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantify_user.R;
import com.example.plantify_user.model.CheckOutModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewItemHolder> {

    Context context;
    ArrayList<CheckOutModel>  CheckList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public CheckOutAdapter (Context context,  ArrayList<CheckOutModel>  CheckList) {
        this.context = context;
        this.CheckList = CheckList;
    }

    @NonNull
    @Override
    public CheckOutAdapter.ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_card, parent, false);
        return new CheckOutAdapter.ViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.ViewItemHolder holder, int position) {
        CheckOutModel checkout  = CheckList.get(position);
        holder.onBind(checkout);
    }

    @Override
    public int getItemCount() {
        return CheckList.size();
    }


    public class ViewItemHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView productname, totalPrice;
        MaterialButton checkOut;
        String productKey;
        String imageUrl;
        String productDescription;
        String productQuantities;
        String userid;
        String realPrice;
        String cartkey;



        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cartproductLogo);
            productname = itemView.findViewById(R.id.cartproductName);
            totalPrice = itemView.findViewById(R.id.cartproductPrice);
            checkOut = itemView.findViewById(R.id.checkOutBtn);

          }
        private void onBind(CheckOutModel checkOutModel) {
            // Retrieving all product details from the model
            String productPrice = checkOutModel.getPrice();
            String productQuantity = checkOutModel.getQuantity();

            int price = Integer.parseInt(productPrice);
            int quantity = Integer.parseInt(productQuantity);

            // Assigning values to member variables
            cartkey = checkOutModel.getCartKey();
            productKey = checkOutModel.getProductKey();
            imageUrl = checkOutModel.getImageUrl();
            productDescription = checkOutModel.getProductDescription();
            productQuantities = checkOutModel.getQuantity();
            realPrice = checkOutModel.getPrice();
            userid = checkOutModel.getUserid();  // Assuming CartModel has a user ID field

            // Loading image and setting text
            Glide.with(itemView.getContext()).load(imageUrl).into(imageView);
            productname.setText(checkOutModel.getProductName());
            totalPrice.setText("Amount " + price + " x " + quantity + " = PHP " + (price * quantity));
        }
    }
}
