package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Estructuras.Pila;
import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;
import com.proyect.yapp_alpha_00.Fragment.DiscusionFragment;
import com.proyect.yapp_alpha_00.Fragment.HomeFragment;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActiviy extends AppCompatActivity {

    ImageView post_image, category, image_profile, close;
    ImageButton publicador;
    TextView username, title, description;
    String authorID, postID;
    BottomNavigationView bottomNavigationView;
    Pila<Post> postStack;
    Pila<Post> postStackPrevius;
    int MenuBottomId;
    boolean oficial;

    private List<String> followingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        post_image = findViewById(R.id.post_image);
        category = findViewById(R.id.category);
        image_profile = findViewById(R.id.image_profile);
        publicador = findViewById(R.id.publicador);
        username = findViewById(R.id.username);
        title = findViewById(R.id.post_title);
        description = findViewById(R.id.post_description);
        close = findViewById(R.id.close);

        postStack = new Pila<>();
        postStackPrevius = new Pila<>();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation_news);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_right).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_left).setChecked(true);

        publicador.setVisibility(View.GONE);

        oficial = false;

        checkFragment();

    }

    private void checkFragment(){
        Intent intent = getIntent();
        postID = intent.getExtras().getString("postID");
        authorID = intent.getExtras().getString("authorID");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("publicador oficial").exists()){
                    oficial = true;
                }
                checkPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        case R.id.nav_right:
                            if(postStack.size > 1) {
                                postStackPrevius.push(postStack.pop());
                                Post noticia = postStack.peek();
                                Glide.with(PostDetailsActiviy.this).load(noticia.getPostimg()).into(post_image);
                                title.setText(noticia.getPosttitulo());
                                description.setText(noticia.getDescripcion());
                                getUserInfo(noticia.getUsuario());
                                getCategory(noticia.getCategoria());
                            }
                            break;
                        case R.id.nav_left:
                            if(!postStackPrevius.isEmpty()) {
                                postStack.push(postStackPrevius.pop());
                                if(!postStack.isEmpty()) {
                                    Post noticia = postStack.peek();
                                    Glide.with(PostDetailsActiviy.this).load(noticia.getPostimg()).into(post_image);
                                    title.setText(noticia.getPosttitulo());
                                    description.setText(noticia.getDescripcion());
                                    getUserInfo(noticia.getUsuario());
                                    getCategory(noticia.getCategoria());
                                    item.setChecked(true);
                                }
                            }
                            break;
                    }

                    return true;

                }
            };

    private void getPost(String postID, String authorID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(postID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    while(!postStack.isEmpty()){
                        if(postStack.peek().getPostid().equals(postID)){
                            Post post = snapshot.getValue(Post.class);
                            Glide.with(PostDetailsActiviy.this).load(post.getPostimg()).into(post_image);
                            title.setText(post.getPosttitulo());
                            description.setText(post.getDescripcion());
                            getUserInfo(authorID);
                            getCategory(post.getCategoria());
                            break;
                        }
                        else{
                            postStackPrevius.push(postStack.pop());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void getUserInfo(String authorID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(authorID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(PostDetailsActiviy.this).load(user.getImg()).into(image_profile);
                username.setText(user.getUsuario());
                if(snapshot.child("publicador oficial").exists()){
                    publicador.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCategory(String categoria){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias").child(categoria);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Categories categories = snapshot.getValue(Categories.class);
                Glide.with(PostDetailsActiviy.this).load(categories.getcImage()).into(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkPosts(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("seguir")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("siguiendo");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    followingList.add(snapshot1.getKey());
                }

                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (!postStack.isEmpty()){
                    postStack.pop();
                }
                Log.w("ESTADO", "COMPROBAR OFICIAL");
                Log.w("ESTADO", String.valueOf(oficial));
                if(oficial) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        if (datasnapshot.child("publicador oficial").exists()) {
                            Post post = datasnapshot.getValue(Post.class);
                            for (String id : followingList) {
                                if (post.getCategoria().equals(id)) {
                                    postStack.push(post);
                                }
                            }
                        }
                    }
                }
                else{
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        if (!datasnapshot.child("publicador oficial").exists()) {
                            Post post = datasnapshot.getValue(Post.class);
                            for (String id : followingList) {
                                if (post.getCategoria().equals(id)) {
                                    postStack.push(post);
                                }
                            }
                        }
                    }
                }

                getPost(postID, authorID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}