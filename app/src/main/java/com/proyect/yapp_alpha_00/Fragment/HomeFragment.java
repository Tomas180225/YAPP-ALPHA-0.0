package com.proyect.yapp_alpha_00.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.PostAdapter;
import com.proyect.yapp_alpha_00.Adapters.UserAdapter;
import com.proyect.yapp_alpha_00.Estructuras.AVLTree;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.PostActivity;
import com.proyect.yapp_alpha_00.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;
    AVLTree avl;
    String filtrar;

    FirebaseUser firebaseUser;
    FloatingActionButton btn_publicar;

    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        btn_publicar = view.findViewById(R.id.btn_upload);

        btn_publicar.setVisibility(View.GONE);

        isPublicator();

        btn_publicar.setOnClickListener(v -> startActivity(new Intent(getActivity(), PostActivity.class)));

        SharedPreferences comprobarFiltro = getActivity().getSharedPreferences("FILTROS", Context.MODE_PRIVATE);
        String filtro = comprobarFiltro.getString("aplicar", "none");

        filtrar = "none";
        if(!filtro.equals("none")){
            filtrar = filtro;
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        avl = new AVLTree();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        checkPosts(recyclerView);

        return view;

    }

    private void checkPosts(RecyclerView recyclerView){

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
                readPosts(recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPosts(RecyclerView recyclerView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    if(datasnapshot.child("publicador oficial").exists()) {
                        Post post = datasnapshot.getValue(Post.class);
                        for (String id : followingList) {
                            if (post.getCategoria().equals(id)) {
                                if(!filtrar.equals("none")) {
                                    if (filtrar.equals("mas_antiguo") || filtrar.equals("mas_reciente")) {
                                        String insertFecha = post.getFecha().substring(0,4)+post.getFecha().substring(5,7)+post.getFecha().substring(8);
                                        avl.root = avl.insert(avl.root, post, insertFecha);
                                    }
                                    else if(post.getCategoria().equals(filtrar)){
                                        postList.add(post);
                                    }
                                }
                                else {
                                    postList.add(post);
                                }
                            }
                        }
                    }
                }
                checkFilter(recyclerView);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFilter(RecyclerView recyclerView){
        if(avl.root != null){
            if(filtrar.equals("mas_antiguo")) {
                avl.postOrder(avl.root);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                postList = avl.getListPosts();
                postAdapter = new PostAdapter(getContext(), postList);
                recyclerView.setAdapter(postAdapter);
            }
            else if(filtrar.equals("mas_reciente")) {
                avl.preOrder(avl.root);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                postList = avl.getListPosts();
                postAdapter = new PostAdapter(getContext(), postList);
                recyclerView.setAdapter(postAdapter);
            }
        }
    }

    private void isPublicator(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference consultar = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        consultar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("publicador oficial").exists()){
                    btn_publicar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("usuario").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    mUser.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search_bar.getText().toString().equals("")){
                    mUser.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        mUser.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    */
}