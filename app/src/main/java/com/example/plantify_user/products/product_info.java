package com.example.plantify_user.products;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantify_user.R;
import com.example.plantify_user.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class product_info extends Fragment {


    String productKey;
    public product_info(String productKey) {
     this.productKey = productKey;
    }

    private  FirebaseDatabase firebaseDatabase;

    private  ImageView productImg, CommentBtn,AddCartBtn;

    private  TextView productName,productPrice,productDesc;

    private  MaterialButton Buy ;

    private  RatingBar ratingBar;

    private  RecyclerView Comments;

    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);
        firebaseAuth = FirebaseAuth.getInstance();


        String userid = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Products Details
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productDesc = view.findViewById(R.id.productDesc);
        productImg = view.findViewById(R.id.productImg);


        //Product Rating Stars
        ratingBar = view.findViewById(R.id.ratingBar);

        //Product Comments not yet Implent A RecyclerView
        Comments = view.findViewById(R.id.Comments);

        //Cart Button
        AddCartBtn = view.findViewById(R.id.AddCart);
        // Buy Button
        Buy = view.findViewById(R.id.BuyBtn);





        //Bar Rating
        ratingBar.getRating();

        DisplayProductInfo();

        AddCartBtn.setOnClickListener(v->{

            getproduct();
        });


        Buy.setOnClickListener(v->{

            BuyProduct();

        });






        return view ;
    }


    private void BuyProduct(){

        AlertDialog.Builder AddCart = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.product_qty_layout,null,false);

        AddCart.setView(view);
        TextInputEditText Quantity;
        Quantity = view.findViewById(R.id.QuantityCart);

        AddCart.setTitle("How Many");

        AddCart.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                firebaseDatabase.getReference("Products").child(productKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProductModel productModel = snapshot.getValue(ProductModel.class);
                        String userid = firebaseAuth.getCurrentUser().getUid();
                        String Qty = Quantity.getText().toString();
                        String key = snapshot.getKey();

                        Map<String,Object> Description = new HashMap<>();
                        Description.put("ImageUrl",productModel.getImageUrl());
                        Description.put("Price",productModel.getPrice());
                        Description.put("ProductDescription",productModel.getProductDescription());
                        Description.put("ProductName",productModel.getProductName());
                        Description.put("Quantity", Qty);





                        if(!Qty.isEmpty()){
                            replaceFragment(new confirm_order(Description,userid,key));

                        }else{
                            Toast.makeText(getContext(), "Filled the Quantities", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        AddCart.show();
    }





    private void getproduct (){


        AlertDialog.Builder AddCart = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.product_qty_layout,null,false);

        AddCart.setView(view);
        TextInputEditText Quantity;
        Quantity = view.findViewById(R.id.QuantityCart);

        AddCart.setTitle("Add To Cart");

        AddCart.setPositiveButton("Add To Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                firebaseDatabase.getReference("Products").child(productKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProductModel productModel = snapshot.getValue(ProductModel.class);
                        String userid = firebaseAuth.getCurrentUser().getUid();
                        String Qty = Quantity.getText().toString();
                        String key = snapshot.getKey().toString();
                        String UniqueId = String.valueOf(System.currentTimeMillis());

                        Map<String,Object> Description = new HashMap<>();
                        Description.put("ImageUrl",productModel.getImageUrl());
                        Description.put("Price",productModel.getPrice());
                        Description.put("ProductDescription",productModel.getProductDescription());
                        Description.put("ProductName",productModel.getProductName());
                        Description.put("Quantity", Qty);


                        if(!Qty.isEmpty()){
                            firebaseDatabase.getReference("Cart").child(UniqueId).setValue(Description).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Add To Cart", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Failed to Add Cart", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getContext(), "Filled the Quantities", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        AddCart.show();

    }





    private void DisplayProductInfo(){
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
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();


    }


}


