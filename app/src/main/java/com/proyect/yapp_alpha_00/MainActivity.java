package com.proyect.yapp_alpha_00;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;
import com.proyect.yapp_alpha_00.Fragment.DiscusionFragment;
import com.proyect.yapp_alpha_00.Fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {


    SearchView buscar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer_menu);

        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        //Menú desplegable
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_View);
        View navHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            selectorMenu(item);
            return true;
        });


        //Hamburguer icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //BottomNavigation instancia
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


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
    private void selectorMenu(MenuItem item) {
        switch(item.getItemId()){
            case R.id.navPerfil:
                Toast.makeText(MainActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navGuardados:
                Toast.makeText(MainActivity.this, "Colecciones", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navAjustes:
                Toast.makeText(MainActivity.this, "Ajustes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navCerrar:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Cerraste sesión", Toast.LENGTH_SHORT).show();
                onBackPressed();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }
}