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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_admin_new_orders);
        ordersRef= FirebaseDatabase.getInstance ().getReference ();
        ordersList=findViewById (R.id.orders_list);
        ordersList.setLayoutManager (new LinearLayoutManager (this));
    }

    @Override
    protected void onStart() {
        super.onStart ();
        final Set<String> set=new HashSet<> ();
        ordersRef.child ("AdminOrders").child (Prevalent.currentOnlineUser.getPhone ())
                .addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren ())
                        {
                            set.add(d.child ("orderId").getValue ().toString ());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders> ()
                .setQuery (ordersRef.child ("Orders"),AdminOrders.class)
                .build ();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                           if(set.contains (model.getOrderId ())) {
                               holder.productName.setText (model.getPname ());
                               holder.quantity.setText ("Quantity : " + model.getQuantity ());
                               holder.userName.setText ("Name : " + model.getName ());
                               holder.userPhoneNumber.setText ("Phone : " + model.getPhone ());
                               holder.userPrice.setText ("Price : " + model.getPrice () + "$");
                               holder.userDateTime.setText ("Order at : " + model.getDate () + " " + model.getTime ());
                               holder.userShippingAddress.setText ("Shipping Address : " + model.getAddress () + " " + model.getCity ());
                               holder.status.setText (model.getState ());
                               holder.status.setOnClickListener (new View.OnClickListener () {
                                   @Override
                                   public void onClick(View v) {
                                       String newStatus = holder.status.getText ().toString ();
                                       if (newStatus.equals ("NOT SHIPPED"))
                                           newStatus = "SHIPPED";
                                       else if (newStatus.equals ("SHIPPED"))
                                           newStatus = "COMPLETE";

                                       ordersRef.child ("Orders").child (model.getOrderId ()).child("state").setValue (newStatus);
                                        holder.status.setText (newStatus);
                                   }
                               });
                           }
                           else
                               holder.itemView.setLayoutParams(holder.params);



                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.orders_layout,viewGroup,false);
                       return new AdminOrdersViewHolder (view);
                    }
                };
        ordersList.setAdapter (adapter);
         adapter.startListening ();
    }
    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
         public TextView productName,quantity,userName,userPhoneNumber,userPrice,userDateTime,userShippingAddress;
         public Button status;
        public LinearLayout.LayoutParams params;
        public AdminOrdersViewHolder(@NonNull View itemView) {
            super (itemView);
            params = new LinearLayout.LayoutParams(0, 0);
            productName=itemView.findViewById (R.id.Pname);
            quantity=itemView.findViewById (R.id.quantity);
            userName=itemView.findViewById (R.id.order_user_name);
            userPhoneNumber=itemView.findViewById (R.id.order_phone_number);
            userPrice=itemView.findViewById (R.id.order_total_price);
            userDateTime=itemView.findViewById (R.id.order_date_time);
            userShippingAddress=itemView.findViewById (R.id.order_address_city);
            status=itemView.findViewById (R.id.status);
        }
    }
}
