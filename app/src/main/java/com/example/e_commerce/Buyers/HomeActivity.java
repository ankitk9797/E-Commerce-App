package com.example.e_commerce.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.e_commerce.Admin.AdminMaintainProductsActivity;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolder.ProductViewHolder;
import com.example.e_commerce.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);
        Paper.init (this);
        Intent it=getIntent ();


        ProductsRef= FirebaseDatabase.getInstance ().getReference ().child ("Products");
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Home");
        setSupportActionBar (toolbar);
        FloatingActionButton fab = findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                    Intent it = new Intent (getApplicationContext (), CartActivity.class);
                    startActivity (it);

            }
        });
        DrawerLayout drawer = findViewById (R.id.drawer_layout);
        NavigationView navigationView = findViewById (R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener (toggle);
        toggle.syncState ();
        navigationView.setNavigationItemSelectedListener (this);
        View headerView=navigationView.getHeaderView (0);
        TextView userNameTextView=headerView.findViewById (R.id.user_profile_name);
        CircleImageView profileImageView=headerView.findViewById (R.id.user_profile_image);

            userNameTextView.setText (Prevalent.currentOnlineUser.getName ());
            Picasso.get ().load (Prevalent.currentOnlineUser.getImage ()).placeholder (R.drawable.profile).into (profileImageView);

        recyclerView=findViewById (R.id.recycler_menu);
        recyclerView.setHasFixedSize (true);
        layoutManager=new LinearLayoutManager (this);
        recyclerView.setLayoutManager (layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart ();
        FirebaseRecyclerOptions<Products>options=
                new FirebaseRecyclerOptions.Builder<Products> ()
                        .setQuery (ProductsRef,Products.class)
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
                                     intent.putExtra("admin",model.getAdmin ());
                                    startActivity (intent); 


                            }
                        });
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById (R.id.drawer_layout);
        if (drawer.isDrawerOpen (GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            Intent intent=new Intent (HomeActivity.this,SearchProductActivity.class);
            startActivity (intent);
        }

        return super.onOptionsItemSelected (item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ();

        if (id == R.id.nav_cart) {

                Intent it=new Intent (getApplicationContext (),CartActivity.class);
                startActivity (it);


        } else if (id == R.id.myorders) {
            Intent it=new Intent (getApplicationContext (),MyOrders.class);
            startActivity (it);

        } else if (id == R.id.nav_settings) {

                Intent intent=new Intent (HomeActivity.this,SettingsActivity.class);
                startActivity (intent);



        } else if (id == R.id.nav_logout) {

                Paper.book ().destroy ();
                Intent intent=new Intent (HomeActivity.this,MainActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (intent);
                finish ();


        }

        DrawerLayout drawer = findViewById (R.id.drawer_layout);
        drawer.closeDrawer (GravityCompat.START);
        return true;
    }
}
