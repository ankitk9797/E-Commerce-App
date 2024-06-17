package com.example.e_commerce.Buyers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolder.ProductViewHolder;
import com.example.e_commerce.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_search_product);
        inputText=(EditText)findViewById (R.id.search_product_name);
        searchBtn=(Button)findViewById (R.id.search_btn);
        searchList=findViewById (R.id.search_list);
        searchList.setLayoutManager (new LinearLayoutManager (SearchProductActivity.this));
        searchBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                SearchInput=inputText.getText ().toString ();
                onStart ();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart ();
        DatabaseReference reference= FirebaseDatabase.getInstance ().getReference ().child ("Products");
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products> ()
                .setQuery (reference.orderByChild ("pname").startAt (SearchInput),Products.class)
                .build ();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText (model.getPname ());
//                        holder.txtProductDescription.setText (model.getDescription ());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        holder.itemView.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext (),ProductDetailsActivity.class);
                                intent.putExtra("pid",model.getPid ());
                                startActivity (intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.products_item_layouts,viewGroup,false);
                        ProductViewHolder holder=new ProductViewHolder (view);
                        return holder;
                    }
                };
        searchList.setAdapter (adapter);
        adapter.startListening ();
    }
}
