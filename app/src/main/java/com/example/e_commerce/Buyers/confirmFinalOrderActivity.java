package com.example.e_commerce.Buyers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class confirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_confirm_final_order);
        totalAmount=getIntent ().getStringExtra ("Total Price");
        Toast.makeText (confirmFinalOrderActivity.this, totalAmount, Toast.LENGTH_SHORT).show ();
        nameEditText=(EditText)findViewById (R.id.shippment_name);
        phoneEditText=(EditText)findViewById (R.id.shippment_phone_number);
        addressEditText=(EditText)findViewById (R.id.shippment_address);
        cityEditText=(EditText)findViewById (R.id.shippment_city);
        confirmOrderBtn=(Button)findViewById (R.id.confirm_final_order_btn);
        confirmOrderBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {
        if(TextUtils.isEmpty (nameEditText.getText ().toString ()))
        {
            Toast.makeText (getApplicationContext (),"Please provide your full name...",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (phoneEditText.getText ().toString ()))
        {
            Toast.makeText (getApplicationContext (),"Please provide your phone number...",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (addressEditText.getText ().toString ()))
        {
            Toast.makeText (getApplicationContext (),"Please provide your address...",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (cityEditText.getText ().toString ()))
        {
            Toast.makeText (getApplicationContext (),"Please provide your city...",Toast.LENGTH_LONG).show ();
        }
        else
        {
            FirebaseDatabase.getInstance ().getReference ().child("Cart List").child(Prevalent.currentOnlineUser.getPhone ())
                    .addValueEventListener (new ValueEventListener () {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d:dataSnapshot.getChildren ())
                            {
                                confirmOrder (d.child("pid").getValue ().toString (),d.child("admin").getValue ().toString ()
                                ,d.child ("quantity").getValue ().toString (),d.child ("price").getValue ().toString (),d.child ("pname").getValue ().toString ());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            FirebaseDatabase.getInstance ().getReference ().child ("Cart List")
                    .child (Prevalent.currentOnlineUser.getPhone ())
                    .removeValue ()
                    .addOnCompleteListener (new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful ())
                            {
                                Toast.makeText (getApplicationContext (),"Your Order has been placed",Toast.LENGTH_LONG).show ();
                                Intent it=new Intent(getApplicationContext (), HomeActivity.class);
                                it.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity (it);
                            }
                        }
                    });
        }
    }

    private void confirmOrder(final String productId,final String Admin,final String quantity,final String price,final String pname) {
        final String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat ("MMM dd,yyyy");
        saveCurrentDate=currentDate.format (calForDate.getTime ());
        SimpleDateFormat currentTime=new SimpleDateFormat ("HH:mm:ss a");
        saveCurrentTime=currentTime.format (calForDate.getTime ());
        final String productRandomKey=saveCurrentDate+saveCurrentTime;
        DatabaseReference ordersRef= FirebaseDatabase.getInstance ().getReference ().child ("Orders").child(productRandomKey);
        final DatabaseReference myOrdersRef= FirebaseDatabase.getInstance ().getReference ().child ("MyOrders");
        final DatabaseReference adminOrdersRef= FirebaseDatabase.getInstance ().getReference ().child ("AdminOrders");
        HashMap<String,Object> orderMap=new HashMap<> ();
        //orderMap.put("totalAmount",totalAmount);
        orderMap.put("orderId",productRandomKey);

        orderMap.put("pid",productId);
        orderMap.put("quantity",quantity);
        orderMap.put("price",price);
        orderMap.put("pname",pname);
        orderMap.put ("name",nameEditText.getText ().toString ());
        orderMap.put ("phone",phoneEditText.getText ().toString ());
        orderMap.put ("address",addressEditText.getText ().toString ());
        orderMap.put ("city",cityEditText.getText ().toString ());
        orderMap.put ("date",saveCurrentDate);
        orderMap.put ("time",saveCurrentTime);
        orderMap.put ("state","NOT SHIPPED");
        ordersRef.updateChildren (orderMap).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful ())
                {
                    HashMap<String,Object> myOrderMap=new HashMap<> ();
                    myOrderMap.put("orderId",productRandomKey);
                    myOrderMap.put("pid",productId);
                    myOrdersRef.child(Prevalent.currentOnlineUser.getPhone ()).child(productRandomKey).updateChildren (myOrderMap);
                    HashMap<String,Object> adminOrderMap=new HashMap<> ();
                    adminOrderMap.put("orderId",productRandomKey);
                    adminOrderMap.put("pid",productId);
                    adminOrderMap.put("user",Prevalent.currentOnlineUser.getPhone ());
                    adminOrdersRef.child(Admin).child(productRandomKey).updateChildren (adminOrderMap);

                }
            }
        });

    }
}
