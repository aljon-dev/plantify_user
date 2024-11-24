package com.example.plantify_user.userSettings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantify_user.R;
import com.example.plantify_user.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class userSettings extends Fragment {


    public userSettings() {
        // Required empty public constructor
    }

    private int PickImageCode = 3;
    private EditText usernameEditText,contactEditText,addressEditText,zipcodeEditText;
    private ImageView    profileImage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private Uri uri;
    private Bitmap bitmap;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private MaterialButton uploadPicture,editButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        String userid = firebaseAuth.getUid();

        usernameEditText = view.findViewById(R.id.usernameEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        zipcodeEditText = view.findViewById(R.id.zipcodeEditText);
        profileImage = view.findViewById(R.id.profileImage);
        uploadPicture = view.findViewById(R.id.uploadPicture);
        editButton = view.findViewById(R.id.editButton);


        profileImage.setOnClickListener(v->{

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PickImageCode);

        });

       editButton.setOnClickListener(v->{
           EdituserBasicInfo(userid);
        });


       uploadPicture.setOnClickListener(v->{
           uploadImageToFirebase(bitmap);
       });


        firebaseDatabase.getReference("Users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                usernameEditText.setText(userModel.getUsername());
                contactEditText.setText(userModel.getContact());
                addressEditText.setText(userModel.getAddress());
                zipcodeEditText.setText(userModel.getZipcode());
                Glide.with(getContext())
                        .load(userModel.getProfile())
                        .error(R.drawable.plant_logo)
                        .error(R.drawable.plant_logo)
                        .into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
        // getting the image from the gallery or kuha ng image sa gallery at iupload
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickImageCode && data.getData() != null ) {
             uri = data.getData();
             try{
                 bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                 profileImage.setImageBitmap(bitmap);
             }catch (IOException e){
                 throw new RuntimeException(e);

             }
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap){
        String userid = firebaseAuth.getUid();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imageBytes = baos.toByteArray();

        String uuid = UUID.randomUUID().toString();


      StorageReference storageReference = firebaseStorage.getReference().child("Images" + uuid);

      storageReference.putBytes(imageBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> Profile = new HashMap<>();
                            Profile.put("Profile",uri.toString());
                         firebaseDatabase.getReference("Users").child(userid).updateChildren(Profile);
                            Toast.makeText(getContext(), "Profile Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
          }
      });
    }


    private void EdituserBasicInfo(String userid){

        Map<String,Object> EditUser = new HashMap<>();

        EditUser.put("Contact" , contactEditText.getText().toString());
        EditUser.put("address",addressEditText.getText().toString());
        EditUser.put("username",usernameEditText.getText().toString());
        EditUser.put("zipcode",zipcodeEditText.getText().toString());

        Toast.makeText(getContext(), "User Info Updated", Toast.LENGTH_SHORT).show();
    }
}