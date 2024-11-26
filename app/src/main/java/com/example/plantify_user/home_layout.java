package com.example.plantify_user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import com.example.plantify_user.adapter.ProductAdapter;
import com.example.plantify_user.model.ProductModel;
import com.example.plantify_user.products.product_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class home_layout extends Fragment {



    public home_layout() {

    }

    private GridView productListed;
    private ArrayList<ProductModel>  productList;

    private ProductAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_layout, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        searchView = view.findViewById(R.id.searchItem);
        firebaseDatabase = FirebaseDatabase.getInstance();


        productListed = view.findViewById(R.id.productListed);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(),productList);
        productListed.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.onItemClickListener() {
            @Override
            public void OnClick(ProductModel productModel) {

                replaceFragment(new product_info(productModel.getKey()));


            }
        });


        firebaseDatabase.getReference("Products").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProductModel productModel = ds.getValue(ProductModel.class);
                    productList.add(productModel);
                    productModel.setKey(ds.getKey());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProductList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductList(newText);
                return true;
            }
        });


        return view;
    }

    private void filterProductList(String query) {
        if (query.isEmpty()) {

            adapter.updateList(productList);
        } else {
            ArrayList<ProductModel> filteredList = new ArrayList<>();
            for (ProductModel product : productList) {
                if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }

            adapter.updateList(filteredList);
        }
    }


    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();

    }




}