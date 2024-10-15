package com.example.plantify_user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewItemHolder> {

     Context context;
     ArrayList<CartModel> cartList;

    public CartAdapter(Context context,ArrayList<CartModel> cartList){

        this.context = context;
        this.cartList = cartList;

    }



    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card_checkout,parent,false);

        return new ViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        CartModel cartModel = cartList.get(position);

        holder.onBind(cartModel);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    public class ViewItemHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView productname, totalPrice;
        MaterialButton checkOut;


        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cartproductLogo);
            productname = itemView.findViewById(R.id.cartproductName);
            totalPrice = itemView.findViewById(R.id.cartproductPrice);
            checkOut = itemView.findViewById(R.id.checkOutBtn);

            checkOut.setOnClickListener(v->{
                Toast.makeText(context, productname.getText().toString(), Toast.LENGTH_SHORT).show();
            });

        }

        private void onBind(CartModel cartModel){

            String productPrice = cartModel.getPrice();
            String productQuantity = cartModel.getQuantity();

            int price = Integer.parseInt(productPrice);
            int quantity = Integer.parseInt(productQuantity);



            Glide.with(itemView.getContext()).load(cartModel.getImageUrl()).into(imageView);

            productname.setText(cartModel.getProductName());
            totalPrice.setText("Amount" +price +"X" + quantity + "    " +"PHP" + price*quantity);




        }

    }

}
