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

public class MainActivity extends AppCompatActivity {

    private MaterialButton materialButton;
    private EditText user_email,user_password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private TextView RegisterBtnTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);


        materialButton = findViewById(R.id.button_login);

        RegisterBtnTxt = findViewById(R.id.Register);

        RegisterBtnTxt.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this,Register.class);
            startActivity(intent);

        });


        materialButton.setOnClickListener(v->{

            String email = user_email.getText().toString();
            String password = user_password.getText().toString();

            Login(email,password);

        });
    }
    private void Login(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser users = firebaseAuth.getCurrentUser();
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("userid",users.getUid());
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




