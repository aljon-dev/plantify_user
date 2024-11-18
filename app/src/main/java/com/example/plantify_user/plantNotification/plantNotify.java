package com.example.plantify_user.plantNotification;

import static android.app.Activity.RESULT_OK;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.example.plantify_user.plantNotification.NotificationReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantify_user.R;
import com.example.plantify_user.adapter.ScheduleAdapter;
import com.example.plantify_user.model.ScheduleModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class plantNotify extends Fragment {


    public plantNotify() {
        // Required empty public constructor
    }
    private static final int IMAGE_PICK_CODE = 1000;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ArrayList<ScheduleModel> scheduleList;
    private FloatingActionButton fab;
    private Uri selectedImageUri;
    private TextView dateTextView, timeTextView;
    private ImageView selectedImageView;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_plant_notify, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(),scheduleList);
        recyclerView.setAdapter(adapter);


        firebaseDatabase.getReference("schedules").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ScheduleModel sched = ds.getValue(ScheduleModel.class);
                    scheduleList.add(sched);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(v->{
            showAddScheduleDialog();
        });



        return view;
    }

    private void scheduleNotification(String plantName, String date, String time) {
        Calendar calendar = Calendar.getInstance();
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");

        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("plantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);

        EditText plantNameEditText = dialogView.findViewById(R.id.EditPlantname);
        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        TextView timeTextView = dialogView.findViewById(R.id.timeTextView);
        ImageView selectedImageView = dialogView.findViewById(R.id.selectedImageView);
        Button addButton = dialogView.findViewById(R.id.addButton);

        scheduleNotification(plantNameEditText.getText().toString(),dateTextView.toString() ,timeTextView.toString());

        // Set up Date Picker
        dateTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        dateTextView.setText(date);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set up Time Picker
        timeTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> {
                        String time = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                        timeTextView.setText(time);
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Image Upload
        selectedImageView.setOnClickListener(v -> openGallery());

        addButton.setOnClickListener(v -> {
            String date = dateTextView.getText().toString();
            String time = timeTextView.getText().toString();
            String plantName = plantNameEditText.getText().toString();

            if (selectedImageUri != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("schedules/" + System.currentTimeMillis() + ".jpg");

                storageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("schedules");
                                    String scheduleId = databaseRef.push().getKey();

                                    Map<String, Object> schedule = new HashMap<>();
                                    schedule.put("date", date);
                                    schedule.put("time", time);
                                    schedule.put("imageUrl", imageUrl);
                                    schedule.put("plantname", plantName);

                                    // Save schedule data to Firebase Realtime Database
                                    databaseRef.child(scheduleId).setValue(schedule)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Schedule added successfully!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Failed to add schedule.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to get image URL.", Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                selectedImageView.setImageURI(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}