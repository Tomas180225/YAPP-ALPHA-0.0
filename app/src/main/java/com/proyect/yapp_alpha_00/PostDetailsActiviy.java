package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;

public class PostDetailsActiviy extends AppCompatActivity {

    ImageView post_image, category, image_profile, close;
    ImageButton publicador;
    TextView username, title, description;
    String authorID, postID;

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

        Intent intent = getIntent();
        postID = intent.getExtras().getString("postID");
        authorID = intent.getExtras().getString("authorID");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        publicador.setVisibility(View.GONE);

        getPost(postID, authorID);

    }
    private void getPost(String postID, String authorID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(PostDetailsActiviy.this).load(post.getPostimg()).into(post_image);
                title.setText(post.getPosttitulo());
                description.setText(post.getDescripcion());
                getUserInfo(authorID);
                getCategory(post.getCategoria());
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
}