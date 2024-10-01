package com.example.plantify_user;

import android.media.Rating;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class product_info extends Fragment {


    String productKey;
    public product_info(String productKey) {
     this.productKey = productKey;
    }

    FirebaseDatabase firebaseDatabase;

    ImageView productImg, CommentBtn,AddCartBtn;

    TextView productName,productPrice,productDesc;

    MaterialButton Buy ;

    RatingBar ratingBar;

    RecyclerView Comments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productDesc = view.findViewById(R.id.productDesc);
        productImg = view.findViewById(R.id.productImg);

        ratingBar = view.findViewById(R.id.ratingBar);
        Comments = view.findViewById(R.id.Comments);

        ratingBar.getRating();

        firebaseDatabase.getReference("Products").child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel productModel = snapshot.getValue(ProductModel.class);

                String totalRatings = productModel.getTotalRating();
                String productRatings = productModel.getProductRating();

                int ratingstotal = totalRatings.isEmpty() ? 1 : Integer.parseInt(totalRatings);
                int ratingProducts = productRatings.isEmpty() ? 1 : Integer.parseInt(productRatings);


                Glide.with(getActivity()).load(productModel.getImageUrl()).error(R.drawable.plant_logo).placeholder(R.drawable.plant_logo).into(productImg);


                productName.setText(productModel.getProductName());
                productPrice.setText(productModel.getPrice());
                productDesc.setText(productModel.getProductDescription());
                ratingBar.setRating((float)ratingProducts /  ratingstotal );



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return view ;
    }
}