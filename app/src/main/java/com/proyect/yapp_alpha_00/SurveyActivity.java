package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapter;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterRegister;
import com.proyect.yapp_alpha_00.Model.Categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.proyect.yapp_alpha_00.R.id.done;

public class SurveyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Categories> categorias;
    List<String> myCategories;
    CategoriesAdapterRegister categoriesAdapter;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    private ImageButton done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        categorias = new ArrayList<>();
        myCategories = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapterRegister(getApplicationContext(), categorias);
        recyclerView.setAdapter(categoriesAdapter);

        auth = FirebaseAuth.getInstance();
        done = findViewById(R.id.done);

        putCategories();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("SELECTED", MODE_PRIVATE);
                Map<String,?> keys = preferences.getAll();
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    myCategories.add(entry.getValue().toString());
                }
                if(keys.size() == 0){
                    Toast.makeText(SurveyActivity.this, "Debes escoger por lo menos una categoria", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd = new ProgressDialog(SurveyActivity.this);
                    pd.setMessage("Danos un segundo");
                    pd.show();
                    Intent intent = getIntent();
                    String str_usuario = intent.getStringExtra("usuario");
                    String str_nombre = intent.getStringExtra("nombre");
                    String str_telefono = intent.getStringExtra("telefono");
                    String str_email = intent.getStringExtra("email");
                    String str_contraseña = intent.getStringExtra("contraseña");
                    Registrar(str_usuario, str_nombre, str_telefono, str_email, str_contraseña, myCategories);
                }
            }
        });
    }

    private void putCategories(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categorias.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Categories categorie = dataSnapshot.getValue(Categories.class);
                    categorias.add(categorie);
                }
                Collections.reverse(categorias);
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Registrar(String usuario, String nombre, String telefono, String email, String contraseña, List myCategories){
        auth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(SurveyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(userId);

                            HashMap<String, Object> hashmap = new HashMap<>();
                            hashmap.put("id", userId);
                            hashmap.put("usuario", usuario.toLowerCase());
                            hashmap.put("nombre", nombre);
                            hashmap.put("telefono", telefono);
                            hashmap.put("bio", "");
                            hashmap.put("img", "https://firebasestorage.googleapis.com/v0/b/yapp-beta.appspot.com/o/usuario.png?alt=media&token=b3fb065b-ed83-4259-96a4-3cf6d31b4f3c");

                            reference.setValue(hashmap);

                            reference = FirebaseDatabase.getInstance().getReference().child("seguir").child(firebaseUser.getUid()).child("siguiendo");

                            HashMap<String, Boolean> hashMap2 = new HashMap<String, Boolean>();
                            for(int i = 0; i < myCategories.size(); i++){
                                hashMap2.put((String) myCategories.get(i), true);
                            }
                            reference.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        startActivity(new Intent(SurveyActivity.this, MainActivity.class));
                                    }
                                }
                            });

                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(SurveyActivity.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}