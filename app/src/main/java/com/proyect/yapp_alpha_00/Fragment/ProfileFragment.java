package com.proyect.yapp_alpha_00.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapter;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterProfile;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    TextView name, username_p, username, cellphone, posts;
    Button button_edit;
    RecyclerView recyclerView;
    CategoriesAdapterProfile categoriesAdapter;
    List<Categories> mCategorias;
    List<String> keys;

    FirebaseUser firebaseUser;
    String profileID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences categorias = getContext().getSharedPreferences("CATEGORIAS", Context.MODE_PRIVATE);
        profileID = prefs.getString("profileID","none");

        image_profile = view.findViewById(R.id.image_profile);
        name = view.findViewById(R.id.name);
        username_p = view.findViewById(R.id.username_p);
        username = view.findViewById(R.id.username);
        cellphone = view.findViewById(R.id.cellphone);
        posts = view.findViewById(R.id.posts);
        button_edit = view.findViewById(R.id.button_edit);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mCategorias = new ArrayList<>();
        keys = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapterProfile(getContext(), mCategorias);
        recyclerView.setAdapter(categoriesAdapter);

        userInfo();
        getPosts();

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(profileID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                Glide.with(getActivity().getApplicationContext()).load(user.getImg()).into(image_profile);
                username_p.setText(user.getUsuario());
                username.setText(user.getUsuario());
                name.setText(user.getNombre());
                cellphone.setText(user.getTelefono());
                putCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getUsuario().equals(profileID)){
                        i++;
                    }
                }

                posts.setText(String.valueOf(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putCategories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("seguir")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("siguiendo");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keys.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    keys.add(dataSnapshot.getKey());
                }
                getCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getCategories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Categories categorie = dataSnapshot.getValue(Categories.class);
                    for(String key: keys){
                        Log.w("ESTADO LLAVE", key);
                        if(categorie.getcName().equals(key)){
                            mCategorias.add(categorie);
                        }
                    }
                }
                Collections.reverse(mCategorias);
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}