package com.proyect.yapp_alpha_00.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.R;

import java.util.List;

public class CategoriesAdapterProfile extends RecyclerView.Adapter<CategoriesAdapterProfile.ViewHolder> {

    private Context mContext;
    private List<Categories> categories;

    public CategoriesAdapterProfile(Context context, List<Categories> categories){
        this.mContext = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_category_item, parent, false);

        return new CategoriesAdapterProfile.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Categories categoriesList = categories.get(position);

        Glide.with(mContext).load(categoriesList.getcImage()).into(holder.category_image);
        holder.category_text.setText(categoriesList.getcName());
        holder.category_image.setTag("unchecked");


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView category_image;
        public TextView category_text;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_text = itemView.findViewById(R.id.category_text);
        }

    }


}
