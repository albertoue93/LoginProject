package com.example.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MyAdapter2 adapter;
    private List<Model> list;
    FloatingActionButton btnFab;
    Button btnCerrarSesion;
    private SearchView txtBuscar;
    TabHost tabs;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btnFab=findViewById(R.id.fab);
        btnCerrarSesion=findViewById(R.id.btnLogout);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtBuscar=findViewById(R.id.txtBuscar);
        Resources res = getResources();

        tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Citas");
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Pendientes");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                startActivity(new Intent(AdminActivity.this, ListActivity2.class));
            }
        });

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        adapter = new MyAdapter2(this, list);
        recyclerView.setAdapter(adapter);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        //Establecer campos
        /*userNombre.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());
        userID.setText(currentUser.getUid());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userImg);*/
        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CIERRA SESION FIREBASE
                mAuth.signOut();
                //Cerrar sesión con google tambien: Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Abrir MainActivity con SigIn button
                        if(task.isSuccessful()){
                            Intent loginActivity = new Intent(getApplicationContext(), loginActivity.class);
                            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loginActivity);
                            AdminActivity.this.finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        ItemTouchHelper touchHelper2 = new ItemTouchHelper(new TouchHelper2(adapter));
        touchHelper2.attachToRecyclerView(recyclerView);
        showData();
        txtBuscar.setOnQueryTextListener(this);

    }
    public void showData(){

        db.collection("Documents").get()
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
                Toast.makeText(AdminActivity.this, "Oops ... something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String termino) {
        adapter.filtrado(termino);
        return false;
    }
    private void signOut() {
        //sign out de firebase
        FirebaseAuth.getInstance().signOut();
        //sign out de "google sign in"
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //regresar al login screen o MainActivity
                //Abrir mainActivity para que inicie sesión o sign in otra vez.
                Intent loginActivity = new Intent(getApplicationContext(), loginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
                AdminActivity.this.finish();
            }
        });
    }

}