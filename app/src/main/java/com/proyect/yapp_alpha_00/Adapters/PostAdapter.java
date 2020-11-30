package com.proyect.yapp_alpha_00.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Estructuras.Pila;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);
        Glide.with(mContext).load(post.getPostimg()).into(holder.post_image);
        if(post.getDescripcion().equals("")){
            holder.description.setVisibility(View.GONE);
        }
        else if(post.getDescripcion().length() > 200){
            holder.title.setText(post.getPosttitulo());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescripcion().substring(0, 165) + "...");
        }
        else{
            holder.title.setText(post.getPosttitulo());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescripcion());
        }

        AuthorInformation(holder.image_profile, holder.username, post.getUsuario());
    }

    @Override
    public int getItemCount() {
        Log.w("TamañoArray", String.valueOf(mPost.size()));
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, comment, save;
        public TextView username, author, title,description, comments;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
        }

    }

    private void AuthorInformation(final ImageView image_profile, final TextView username, final String userID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImg()).into(image_profile);
                username.setText(user.getUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
