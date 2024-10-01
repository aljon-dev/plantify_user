package com.example.plantify_user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private MaterialButton materialButton;
    private EditText user_email,user_password,user_name,user_address,user_contact;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private TextView loginhere ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_name = findViewById(R.id.user_name);
        user_address = findViewById(R.id.user_address);
        user_contact = findViewById(R.id.user_contact);
        loginhere = findViewById(R.id.LoginHere);

        materialButton = findViewById(R.id.Register_btn);

        loginhere.setOnClickListener(v->{
            Intent intent = new Intent(Register.this,MainActivity.class);
            startActivity(intent);
        });



        materialButton.setOnClickListener(v->{
            String email = user_email.getText().toString();
            String pass = user_password.getText().toString();
            String name = user_name.getText().toString();
            String address = user_address.getText().toString();
            String contact = user_contact.getText().toString();

            if(email.isEmpty() || pass.isEmpty() || name.isEmpty() || address.isEmpty() || contact.isEmpty()){
                Toast.makeText(this, "Filled the fields", Toast.LENGTH_SHORT).show();
            }else{
                register(email,pass,name,address,contact);
            }

        });


    }

    private void register(String email,String password,String username,String address,String contact){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                FirebaseUser users = firebaseAuth.getCurrentUser();
                Map<Object,String> RegisterUser = new HashMap<>();
                RegisterUser.put("username",username);
                RegisterUser.put("address",address);
                RegisterUser.put("Contact",contact);
                RegisterUser.put("Profile","");

                firebaseDatabase.getReference().child("Users").child(users.getUid()).setValue(RegisterUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Register.this, "Account Registered Successfully", Toast.LENGTH_SHORT).show();
                        if(users.getUid() != null){
                            Intent intent = new Intent(Register.this,Home.class);
                            startActivity(intent);
                        }
                    }
                });

                }else{
                    Toast.makeText(Register.this, "Account is Either Exist or Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}