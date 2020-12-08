package com.proyect.yapp_alpha_00.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.CommentActivity;
import com.proyect.yapp_alpha_00.Fragment.DiscusionFragment;
import com.proyect.yapp_alpha_00.MainActivity;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.PostDetailsActiviy;
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
        holder.fecha.setText(post.getFecha());
        if(post.getDescripcion().equals("")){
            holder.description.setVisibility(View.GONE);
        }
        else if(post.getDescripcion().length() > 130){
            holder.title.setText(post.getPosttitulo());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescripcion().substring(0, 130) + "...");
        }
        else{
            holder.title.setText(post.getPosttitulo());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescripcion());
        }

        AuthorInformation(holder.image_profile, holder.username, post.getUsuario(), holder.publicador);

        getComments(post.getPostid(), holder.comments);
        isSaved(post.getPostid(), holder.save);
        getCategory(post.getCategoria(), holder.category);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("guardados")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("guardados")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("authorID", post.getUsuario());
                Log.w("ESTADOc", post.getUsuario());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("authorID", post.getUsuario());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mContext.startActivity(intent);
            }
        });

        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailsActiviy.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("authorID", post.getUsuario());
                mContext.startActivity(intent);
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailsActiviy.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("authorID", post.getUsuario());
                mContext.startActivity(intent);
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailsActiviy.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("authorID", post.getUsuario());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, comment, save, category;
        public TextView username, title,description, comments,fecha;
        private ImageButton publicador;

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
            category = itemView.findViewById(R.id.category);
            publicador = itemView.findViewById(R.id.publicador);
            fecha = itemView.findViewById(R.id.fecha);

            publicador.setVisibility(View.GONE);
        }

    }

    private void getComments(String postID, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comentarios").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("Ir al foro de discusion: "+ snapshot.getChildrenCount() + " comentarios");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AuthorInformation(final ImageView image_profile, final TextView username, final String userID, ImageButton publicador){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("publicador oficial").exists()){
                    publicador.setVisibility(View.VISIBLE);
                }
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImg()).into(image_profile);
                username.setText(user.getUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postID, ImageView imageView){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("guardados").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postID).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getCategory(String categoria, ImageView category){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias").child(categoria);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img = snapshot.child("cImage").getValue().toString();
                Glide.with(mContext).load(img).into(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
