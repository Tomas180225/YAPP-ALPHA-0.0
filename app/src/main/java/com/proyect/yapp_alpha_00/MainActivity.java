package com.proyect.yapp_alpha_00;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;
import com.proyect.yapp_alpha_00.Fragment.DiscusionFragment;
import com.proyect.yapp_alpha_00.Fragment.HomeFragment;
import com.proyect.yapp_alpha_00.Fragment.ProfileFragment;
import com.proyect.yapp_alpha_00.Fragment.SavedFragment;
import com.proyect.yapp_alpha_00.Fragment.SettingsFragment;
import com.proyect.yapp_alpha_00.Model.User;

public class MainActivity extends AppCompatActivity {

    SearchView buscar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    int MenuBottomId;
    int MenuDrawerId;
    ImageView profile_image_menu;
    TextView username_menu;
    Fragment selectedFragment = null;

    EditText addComment;
    ImageView image_profile;
    TextView post;
    String postID;
    String AuthorID;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer_menu);

        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        //Menú desplegable
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_View);
        View navHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(item -> {
            selectorMenu(item);
            return true;
        });
        profile_image_menu = navHeader.findViewById(R.id.profile_image_menu);
        username_menu = navHeader.findViewById(R.id.username_menu);


        //Hamburguer icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //BottomNavigation instancia
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_community).setChecked(true);

        addComment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        Intent args = getIntent();
        postID = args.getStringExtra("postID");
        AuthorID = args.getStringExtra("authorID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userInfo();

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            String autorID = intent.getString("authorID");
            String postID = intent.getString("postID");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();

            Log.w("Usuario", firebaseUser.getUid());
            editor.putString("profileID", firebaseUser.getUid());
            editor.putString("postID", postID);
            editor.putString("authorID", autorID);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiscusionFragment()).commit();
            bottomNavigationView.getMenu().findItem(R.id.nav_discusion).setChecked(true);
        }
        else{
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileID", firebaseUser.getUid());
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }



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

                    MenuBottomId = item.getItemId();
                    for(int i = 0; i < bottomNavigationView.getMenu().size(); i++){
                        MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
                        boolean isChecked = menuItem.getItemId() == item.getItemId();
                        menuItem.setChecked(isChecked);
                    }

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

        MenuDrawerId = item.getItemId();
        for(int i = 0; i < navigationView.getMenu().size(); i++){
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == item.getItemId();
            menuItem.setChecked(isChecked);
        }

        switch(item.getItemId()){
            case R.id.navPerfil:
                selectedFragment = new ProfileFragment();
                Toast.makeText(MainActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navGuardados:
                selectedFragment = new SavedFragment();
                Toast.makeText(MainActivity.this, "Colecciones", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navAjustes:
                selectedFragment = new SettingsFragment();
                Toast.makeText(MainActivity.this, "Ajustes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navCerrar:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Cerraste sesión", Toast.LENGTH_SHORT).show();
                onBackPressed();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
        if(selectedFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            drawerLayout.closeDrawers();
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(MainActivity.this).load(user.getImg()).into(profile_image_menu);
                username_menu.setText(user.getUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}