package com.example.mitienditaserver.ViewModel;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitienditaserver.Common.Common;
import com.example.mitienditaserver.Interface.IItemClickListener;
import com.example.mitienditaserver.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductoViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView lblFood_title;
    public ImageView lblFood_image;
    public TextView lblFood_price;
    public TextView lblFood_discount;
    public TextView lblFood_status;

    private IItemClickListener iItemClickListener;

    public ProductoViewHolder(@NonNull View itemView) {
        super(itemView);

        lblFood_title = itemView.findViewById(R.id.food_title);
        lblFood_image = itemView.findViewById(R.id.food_image);
        lblFood_price = itemView.findViewById(R.id.food_price);
        lblFood_discount = itemView.findViewById(R.id.food_discount);
        lblFood_status = itemView.findViewById(R.id.food_status);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }//PlatillosViewHolder


    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.iItemClickListener = itemClickListener;
    }//setItemClickListener

    @Override
    public void onClick(View view) {
        iItemClickListener.onClick(view, getAdapterPosition(), false);
    }//onClick

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Operación");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }//onCreateContextMenu

}//ProductoViewHolder
