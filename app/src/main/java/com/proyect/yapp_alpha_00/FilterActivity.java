package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterFilters;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterRegister;
import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;
import com.proyect.yapp_alpha_00.Model.Categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    Button mas_antiguo, mas_reciente, clear;
    RecyclerView recyclerView;
    List<Categories> categorias;
    List<String> myCategories;
    CategoriesAdapterFilters categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mas_antiguo = findViewById(R.id.mas_antiguos);
        mas_reciente = findViewById(R.id.mas_recientes);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FilterActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        categorias = new ArrayList<>();
        myCategories = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapterFilters(FilterActivity.this, categorias);
        recyclerView.setAdapter(categoriesAdapter);

        putCategories();

        clear = findViewById(R.id.clear);
        SharedPreferences.Editor filtros = getSharedPreferences("FILTROS", MODE_PRIVATE).edit();
        filtros.clear();
        filtros.apply();

        mas_antiguo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtros.putString("aplicar", "mas_antiguo");
                filtros.apply();
                Toast.makeText(FilterActivity.this, "Todas tus noticia se han ordenado desde la mas antigua", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FilterActivity.this, MainActivity.class));
            }
        });

        mas_reciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtros.putString("aplicar", "mas_reciente");
                filtros.apply();
                Toast.makeText(FilterActivity.this, "Todas tus noticia se han ordenado desde la mas reciente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FilterActivity.this, MainActivity.class));
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtros.clear().apply();
                Toast.makeText(FilterActivity.this, "Filtros eliminados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FilterActivity.this, MainActivity.class));
            }
        });
    }

    private void putCategories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
}