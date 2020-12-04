package com.proyect.yapp_alpha_00.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapter;
import com.proyect.yapp_alpha_00.Adapters.PostAdapter;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.R;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private List<String> mSaves;

    RecyclerView recyclerView_saved;
    PostAdapter postAdapter;
    List<Post> mPost;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView_saved = view.findViewById(R.id.recycler_view_saved);
        recyclerView_saved.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView_saved.setLayoutManager(linearLayoutManager);
        mPost = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), mPost);
        recyclerView_saved.setAdapter(postAdapter);

        mySaves();

        return view;

    }

    private void mySaves(){
        mSaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("guardados").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mSaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for(String id: mSaves){
                        if(post.getPostid().equals(id)){
                            mPost.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}