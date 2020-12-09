package com.proyect.yapp_alpha_00.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.R;

import java.util.List;

public class CategoriesAdapterFilters extends RecyclerView.Adapter<CategoriesAdapterFilters.ViewHolder> {

    private Context mContext;
    private List<Categories> categories;
    SharedPreferences.Editor sharedPreferences;

    public CategoriesAdapterFilters(Context context, List<Categories> categories){
        this.mContext = context;
        this.categories = categories;
        Log.w("ESTADO", "ADAPTER");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_category_item, parent, false);

        return new CategoriesAdapterFilters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Categories categoriesList = categories.get(position);
        Log.w("ESTADO", categories.get(position).getcName());

        Glide.with(mContext).load(categoriesList.getcImage()).into(holder.category_image);
        holder.category_text.setText(categoriesList.getcName());

        holder.category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.putString("categoria", categoriesList.getcName());
                sharedPreferences.apply();
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

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_text = itemView.findViewById(R.id.category_text);
            sharedPreferences = mContext.getSharedPreferences("FILTRO_CATEGORIA", Context.MODE_PRIVATE).edit();
        }

    }


}
