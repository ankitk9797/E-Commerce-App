package com.example.e_commerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e_commerce.Interface.ItemClickListner;
import com.example.e_commerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public LinearLayout.LayoutParams params;
    public TextView txtProductName,txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listener;
    public ProductViewHolder(@NonNull View itemView) {
        super (itemView);
        imageView=(ImageView)itemView.findViewById (R.id.product_image);
        txtProductName=(TextView)itemView.findViewById (R.id.product_name);
        txtProductPrice=(TextView)itemView.findViewById (R.id.product_price);
        params = new LinearLayout.LayoutParams(0, 0);
    }
    public void setItemClickListner(ItemClickListner listener)
    {
this.listener=listener;
    }
    @Override
    public void onClick(View v) {
             listener.onClick (v,getAdapterPosition (),false);
    }
}
