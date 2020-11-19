package com.example.mitienditaserver.ViewModel;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitienditaserver.Common.Common;
import com.example.mitienditaserver.R;
import com.example.mitienditaserver.Interface.IItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PromocionViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener  {

    public TextView lblPromotion_Title;
    public ImageView ImgPromotion_image;
    public TextView lblPromotion_RestaurantName;
    public TextView lblPromotion_price;
    public TextView lblPromotion_DateEnd;

    private IItemClickListener iItemClickListener;


    public PromocionViewHolder(@NonNull View itemView) {
        super(itemView);

        lblPromotion_Title = itemView.findViewById(R.id.lblPromotionTitle);
        ImgPromotion_image = itemView.findViewById(R.id.imgPromotionImage);
        lblPromotion_RestaurantName = itemView.findViewById(R.id.lblPromotionRestaurantName);
        lblPromotion_price = itemView.findViewById(R.id.lblPromotionPrice);
        lblPromotion_DateEnd = itemView.findViewById(R.id.lblPromotionDateEnd);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }//PromocionViewHolder

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

}//PromocionViewHolder
