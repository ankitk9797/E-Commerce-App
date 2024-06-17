package com.example.e_commerce.Buyers;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolder.CartViewHolder;
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

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount,txtMsg1;
    private int overTotalPrice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cart);
        recyclerView=findViewById (R.id.cart_list);
        recyclerView.setHasFixedSize (true);
        layoutManager=new LinearLayoutManager (this);
        recyclerView.setLayoutManager (layoutManager);
        nextProcessBtn=(Button)findViewById (R.id.next_process_btn);
        txtTotalAmount=(TextView)findViewById (R.id.total_price);
        txtMsg1=(TextView)findViewById (R.id.msg1);
        nextProcessBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

           Intent it=new Intent(CartActivity.this,confirmFinalOrderActivity.class);
           it.putExtra ("Total Price",String.valueOf (overTotalPrice));
           startActivity (it);
           finish();
            }
        }
        );
    }

    @Override
    protected void onStart() {
        super.onStart ();
           // checkOrderState ();
        final DatabaseReference cartListRef= FirebaseDatabase.getInstance ().getReference ().child ("Cart List");
        FirebaseRecyclerOptions<cart> options=
                new FirebaseRecyclerOptions.Builder<cart> ()
                .setQuery (cartListRef
                .child (Prevalent.currentOnlineUser.getPhone ()),cart.class)
                .build ();
        FirebaseRecyclerAdapter<cart, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<cart, CartViewHolder> (options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final cart model) {
              holder.txtProductQuantity.setText ("Quantity = "+model.getQuantity ());
              holder.txtProductPrice.setText ("Price = "+model.getPrice ()+"$");
              holder.txtProductName.setText ("Name = "+model.getPname ());
              int price=(Integer.parseInt(model.getPrice ()))*(Integer.parseInt (model.getQuantity ()));
              overTotalPrice+=price;
                txtTotalAmount.setText ("Total Price = "+String.valueOf (overTotalPrice)+"$");
              holder.itemView.setOnClickListener (new View.OnClickListener () {
                  @Override
                  public void onClick(View v) {
                      CharSequence[] options=new CharSequence[]{"Edit","Remove"};
                      AlertDialog.Builder builder=new AlertDialog.Builder (CartActivity.this);
                      builder.setTitle ("Cart Options:");
                      builder.setItems (options, new DialogInterface.OnClickListener () {
                          @Override
                          public void onClick(DialogInterface dialog, int id) {
                              if(id==0)
                              {
                                  Intent it=new Intent(getApplicationContext (), ProductDetailsActivity.class);
                                  it.putExtra ("pid",model.getPid ());
                                  it.putExtra ("admin",model.getAdmin ());
                                  startActivity (it);
                              }
                              if(id==1)
                              {
                                  cartListRef.child (Prevalent.currentOnlineUser.getPhone ())
                                          .child (model.getPid ())
                                          .removeValue ()
                                          .addOnCompleteListener (new OnCompleteListener<Void> () {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if(task.isSuccessful ()) {
                                                      Toast.makeText (getApplicationContext (), "Item removed succcessfully", Toast.LENGTH_LONG).show ();
                                                      Intent it=new Intent (getApplicationContext (), HomeActivity.class);
                                                      startActivity (it);
                                                  }
                                              }
                                          });
                              }
                          }
                      });
                      builder.show ();
                  }
              });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.cart_items_layout,viewGroup,false);
                CartViewHolder holder=new CartViewHolder (view);
                return holder;
            }
        };
        recyclerView.setAdapter (adapter);
        adapter.startListening ();

    }
    private void checkOrderState()
    {
        DatabaseReference ordersRef=FirebaseDatabase.getInstance ().getReference ()
                .child ("Orders").child (Prevalent.currentOnlineUser.getPhone ());
        ordersRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ())
                {
                    String shippingState=dataSnapshot.child ("state").getValue().toString ();
                    String userName=dataSnapshot.child ("name").getValue ().toString ();
                    if(shippingState.equals ("shipped"))
                    {
                        txtTotalAmount.setText ("Dear "+userName+"\n order is shipped successfully.");
                        recyclerView.setVisibility (View.GONE);
                        txtMsg1.setText ("Congrtulations, you final order has been shipped successfully. Soon you will received your order at your door step");
                        txtMsg1.setVisibility (View.VISIBLE);
                        nextProcessBtn.setVisibility (View.GONE);
                        Toast.makeText (getApplicationContext (),"you can purchase more products, once you received your first final  order",Toast.LENGTH_LONG).show ();
                    }
                    else if(shippingState.equals ("not shipped"))
                    {
                        txtTotalAmount.setText ("Your order is not shipped yet.");
                        recyclerView.setVisibility (View.GONE);
                        txtMsg1.setVisibility (View.VISIBLE);
                        nextProcessBtn.setVisibility (View.GONE);
                        Toast.makeText (getApplicationContext (),"you can purchase more products, once you received your first final  order",Toast.LENGTH_LONG).show ();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
