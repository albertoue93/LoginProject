package com.example.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ListAdapter2 adapter;
    private List<Model> list;
    TabHost tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        adapter = new ListAdapter2(this, list);
        recyclerView.setAdapter(adapter);
        showData();
        tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pendientes");
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Citas");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                startActivity(new Intent(ListActivity2.this,AdminActivity.class));
            }
        });
    }

    public void showData(){

        db.collection("Documents").whereEqualTo("desc","Pendiente").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){

                            Model model = new Model(
                                    snapshot.getString("id") ,
                                    snapshot.getString("title"),
                                    snapshot.getString("desc"),
                                    snapshot.getString("date"),
                                    snapshot.getString("time"));

                            list.add(model);

                        }
                        Collections.sort(list,new Model.SortByDate());
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListActivity2.this, "Oops ... something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}