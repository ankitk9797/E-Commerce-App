package com.example.e_commerce.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_commerce.Buyers.CartActivity;
import com.example.e_commerce.Buyers.HomeActivity;
import com.example.e_commerce.Buyers.ProductDetailsActivity;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolder.CartViewHolder;
import com.example.e_commerce.ViewHolder.ProductViewHolder;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.model.cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class MyProducts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_my_products);
        recyclerView=findViewById (R.id.myProductsList);
        recyclerView.setHasFixedSize (true);
        layoutManager=new LinearLayoutManager (this);
        recyclerView.setLayoutManager (layoutManager);
    }
    @Override
    protected void onStart() {
        super.onStart ();
        final Set<String> set=new HashSet<String> ();
        DatabaseReference myProductsRef=FirebaseDatabase.getInstance ().getReference ().
                child ("MyProducts").child(Prevalent.currentOnlineUser.getPhone ());
        myProductsRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 for(DataSnapshot dd:dataSnapshot.getChildren ())
                 {
                     set.add(dd.child ("pid").getValue ().toString ());
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference ProductsRef=FirebaseDatabase.getInstance ().getReference ().child ("Products");

        FirebaseRecyclerOptions<Products>options=
                new FirebaseRecyclerOptions.Builder<Products> ()
                        .setQuery (ProductsRef,Products.class)
                        .build ();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        if(set.contains(model.getPid ())) {
                            holder.txtProductName.setText (model.getPname ());
//                            holder.txtProductDescription.setText (model.getDescription ());
                            holder.txtProductPrice.setText ("Price = " + model.getPrice () + "$");
                            Picasso.get ().load (model.getImage ()).into (holder.imageView);
                            holder.itemView.setOnClickListener (new View.OnClickListener () {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent (getApplicationContext (), AdminMaintainProductsActivity.class);
                                    intent.putExtra ("pid", model.getPid ());
                                    startActivity (intent);

                                }
                            });
                        }
                        else
                        {
                            holder.itemView.setLayoutParams(holder.params);
                        }
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from (parent.getContext ()).inflate (R.layout.products_item_layouts,parent,false);
                        ProductViewHolder holder=new ProductViewHolder (view);
                        return holder;
                    }
                };
        recyclerView.setAdapter (adapter);
        adapter.startListening ();
    }

}
