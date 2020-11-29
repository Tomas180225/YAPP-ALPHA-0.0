package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.proyect.yapp_alpha_00.Fragment.HomeFragment;
import com.proyect.yapp_alpha_00.Fragment.DiscusionFragment;
import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;

public class MainActivity extends AppCompatActivity {


    EditText buscar;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buscar = findViewById(R.id.top_search);
/*
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar.setHint("Buscar");
                buscar.setBackgroundResource(R.drawable.posts_background);
            }
        });
*/
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_noticias:
                            selectedFragment = new CommunityFragment();
                            break;
                        case R.id.nav_discusion:
                            selectedFragment = new DiscusionFragment();
                            break;
                        case R.id.nav_community:
                            selectedFragment = new HomeFragment();
                            break;
                    }

                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    }

                    return true;

                }
            };
}