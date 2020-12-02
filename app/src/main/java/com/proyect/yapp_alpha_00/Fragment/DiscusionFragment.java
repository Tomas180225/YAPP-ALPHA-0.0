package com.proyect.yapp_alpha_00.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Adapters.CommentAdapter;
import com.proyect.yapp_alpha_00.CommentActivity;
import com.proyect.yapp_alpha_00.Model.Comment;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscusionFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText addComment;
    ImageView image_profile;
    ImageView post_image;
    TextView username;
    TextView title;
    TextView description;
    TextView post;

    String postID;
    String AuthorID;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discusion, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(commentAdapter);

        addComment = view.findViewById(R.id.add_comment);
        image_profile = view.findViewById(R.id.image_profile);
        post = view.findViewById(R.id.post);
        post_image = view.findViewById(R.id.post_image);
        username = view.findViewById(R.id.username);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        AuthorID = sharedPreferences.getString("authorID", "none");
        postID = sharedPreferences.getString("postID", "none");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "No puedes añadir un comentario vacio", Toast.LENGTH_SHORT).show();
                }
                else {
                    add_comment();
                }
            }
        });

        getUserInfoPost();
        getPost();
        getImage();
        readComments();

        return view;
    }

    private void add_comment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comentarios").child(postID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comentario", addComment.getText().toString());
        hashMap.put("authorID", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addComment.setText("");
    }

    private void getImage(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getActivity().getApplicationContext()).load(user.getImg()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readComments(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("comentarios").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfoPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(AuthorID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getActivity().getApplicationContext()).load(user.getImg()).into(image_profile);
                username.setText(user.getUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(getActivity().getApplicationContext()).load(post.getPostimg()).into(post_image);
                title.setText(post.getPosttitulo());
                if(post.getDescripcion().length() > 165){
                    description.setText(post.getDescripcion().substring(0, 165) + "...");
                }
                else{
                    description.setText(post.getDescripcion());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}