package com.example.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private EditText mTitle;
    private Button mSaveBtn, mShowBtn;
    private FirebaseFirestore db;
    private String uTitle, uDesc , uId;

    private Button mDatebtn , mTimebtn;
    private TextView mDateText , mTimeText, mDesc;
    private String uDate, uTime;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mTitle = findViewById(R.id.edit_title);
        mSaveBtn = findViewById(R.id.save_btn);
        mShowBtn = findViewById(R.id.showall_btn);
        mDesc=findViewById(R.id.edit_desc);


        mDatebtn = findViewById(R.id.datebtn);
        mTimebtn = findViewById(R.id.timebtn);
        mDateText = findViewById(R.id.datetext);
        mTimeText = findViewById(R.id.textView2);

        db= FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uDesc");
            uDesc = bundle.getString("uDesc");
            uDate = bundle.getString("uDate");
            uTime = bundle.getString("uTime");
        } else {
            mSaveBtn.setText("Save");
        }

        if (bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uId");
            uDesc = bundle.getString("uDesc");

            mTitle.setText(uTitle);
            mDesc.setText(uDesc);

            uDate = bundle.getString("uDate");
            uTime = bundle.getString("uTime");
            mDateText.setText(uDate);
            mTimeText.setText(uTime);

        }else{
            mSaveBtn.setText("Save");
        }
        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mTitle.getText().toString();
                String desc = spinner.getSelectedItem().toString();
                String date = mDateText.getText().toString();
                String time = mTimeText.getText().toString();


                Bundle bundle1 = getIntent().getExtras();
                if(bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, title, desc, date, time);
                } else {
                    String id = UUID.randomUUID().toString();
                    saveToFirestore(id, title, desc, date, time);
                }
            }
        });



        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        mTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
         spinner=  findViewById(R.id.planets_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    private void updateToFireStore(String id, String title, String desc, String date, String time ) {
        db.collection("Documents").document(id).update(
                "title", title,
                "desc", desc,
                "date", date,
                "time", time
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Data update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirestore(String id, String title, String desc, String date, String time){
        if (!title.isEmpty() && !desc.isEmpty()){
            HashMap<String , Object> map = new HashMap<>();
            map.put("id" , id);
            map.put("title" , title);
            map.put("desc" , desc);
            map.put("date" , date);
            map.put("time" , time);

            db.collection("Documents").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Data Saved !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                }
            });

        }else
            Toast.makeText(this, "Empty Fields not Allowed", Toast.LENGTH_SHORT).show();
    }

    private void openTimePicker() {

        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTimeText.setText(hourOfDay + ":" + minute);
            }
        } ,  HOUR , MINUTE , false);
        timePickerDialog.show();
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                mDateText.setText(dayOfMonth + "/" + month + "/" + year);
            }
        } , YEAR , MONTH , DATE);
        datePickerDialog.show();
    }

}
