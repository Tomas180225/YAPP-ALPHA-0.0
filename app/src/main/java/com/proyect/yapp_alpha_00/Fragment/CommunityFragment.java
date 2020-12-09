package com.proyect.yapp_alpha_00.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.proyect.yapp_alpha_00.Adapters.PostAdapter;
import com.proyect.yapp_alpha_00.Estructuras.AVLTree;
import com.proyect.yapp_alpha_00.Estructuras.ArbolDeBusqueda;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.PostActivity;
import com.proyect.yapp_alpha_00.R;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;
    AVLTree avl;
    String filtrar;

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        FloatingActionButton btn_publicar = view.findViewById(R.id.btn_upload);

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
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    if(!datasnapshot.child("publicador oficial").exists()) {
                        Post post = datasnapshot.getValue(Post.class);
                        for (String id : followingList) {
                            if (post.getCategoria().equals(id)) {
                                if(!filtrar.equals("none")) {
                                    if (filtrar.equals("mas_antiguo") || filtrar.equals("mas_reciente")) {
                                        String insertFecha = post.getFecha().substring(0,4)+post.getFecha().substring(5,7)+post.getFecha().substring(8);
                                        avl.root = avl.insert(avl.root, post, insertFecha);
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
                Log.w("PRIMERO",postList.get(0).getPosttitulo());
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
}