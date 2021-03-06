package com.proyect.yapp_alpha_00.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.PostActivity;
import com.proyect.yapp_alpha_00.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context mContext;
    private List<Categories> categories;
    int selected = 0;

    public CategoriesAdapter(Context context, List<Categories> categories){
        this.mContext = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);

        return new CategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Categories categoriesList = categories.get(position);

        Glide.with(mContext).load(categoriesList.getcImage()).into(holder.category_image);
        holder.category_text.setText(categoriesList.getcName());
        holder.category_image.setTag("unchecked");

        holder.category_selected.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mContext.getSharedPreferences("SELECTED", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        holder.category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.category_image.getTag().equals("unchecked") && selected == 0){
                    holder.category_image.setTag("checked");
                    holder.category_selected.setVisibility(View.VISIBLE);
                    selected += 1;
                    editor.putString("cName", categoriesList.getcName());
                    editor.apply();
                }
                else if(holder.category_image.getTag().equals("checked")){
                    holder.category_image.setTag("unchecked");
                    holder.category_selected.setVisibility(View.GONE);
                    selected = 0;
                    editor.clear();
                    editor.apply();
                }
                else{
                    Toast.makeText(v.getContext(), "Solo puedes seleccionar una categoria", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView category_image;
        public TextView category_text;
        public ImageView category_selected;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_text = itemView.findViewById(R.id.category_text);
            category_selected = itemView.findViewById(R.id.selected);
        }

    }


}
