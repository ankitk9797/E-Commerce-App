package com.example.e_commerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.e_commerce.Interface.ItemClickListner;
import com.example.e_commerce.R;


public class CartViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductPrice,txtProductQuantity;
    private ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super (itemView);
        txtProductName=itemView.findViewById (R.id.cart_product_name);
        txtProductPrice=itemView.findViewById (R.id.cart_product_price);
        txtProductQuantity=itemView.findViewById (R.id.cart_product_quantity);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
       itemClickListner.onClick (v,getAdapterPosition (),false);
    }
}
