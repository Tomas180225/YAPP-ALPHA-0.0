package com.proyect.yapp_alpha_00;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;

public class FilterActivity extends AppCompatActivity {

    Button mas_antiguo, mas_reciente, clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mas_antiguo = findViewById(R.id.mas_antiguos);
        mas_reciente = findViewById(R.id.mas_recientes);
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
}