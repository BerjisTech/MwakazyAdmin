package tech.berjis.mwakazyadmin;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    List<Categories> listData;
    Context mContext;

    AllCategoriesAdapter(Context mContext, List<Categories> listData) {
        this.mContext = mContext;
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Categories ld = listData.get(position);

        holder.name.setText(ld.getName());

        Picasso.get().load(ld.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c_intent = new Intent(holder.itemView.getContext(), ByCategoryActivity.class);
                Bundle c_bundle = new Bundle();
                c_bundle.putString("category", ld.getName());
                c_intent.putExtras(c_bundle);
                holder.itemView.getContext().startActivity(c_intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent c_intent = new Intent(holder.itemView.getContext(), CategoryExtrasActivity.class);
                Bundle c_bundle = new Bundle();
                c_bundle.putString("category", ld.getCategoryId());
                c_intent.putExtras(c_bundle);
                holder.itemView.getContext().startActivity(c_intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }
}
