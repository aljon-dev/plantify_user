package com.example.plantify_user;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {

    private ImageView ImageMenu;


    private NavigationView nav;
    private ImageView imagebutton;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nav = findViewById(R.id.home_menu);
        drawer = findViewById(R.id.drawer);
        imagebutton = findViewById(R.id.imageButton);



        setFragment(new home_layout());

        imagebutton.setOnClickListener(v->{
            drawer.open();
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemid  = menuItem.getItemId();

                if(itemid == R.id.Home){
                    setFragment(new home_layout());
                    Toast.makeText(Home.this, "Home", Toast.LENGTH_SHORT).show();
                }

                if(itemid == R.id.Orders){

                }
                if(itemid == R.id.Chats){

                }
                if(itemid == R.id.FeedBacks){

                }
                if(itemid == R.id.Sign_Out){

                }
                return false;
            }
        });




    }

    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();

    }
}