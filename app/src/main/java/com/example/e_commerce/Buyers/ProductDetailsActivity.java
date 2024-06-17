package com.example.e_commerce.Buyers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription,productName;
    private String productID="",admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_product_details);
        addToCartBtn=(Button)findViewById (R.id.pd_add_to_cart_button);
        numberButton=(ElegantNumberButton)findViewById (R.id.number_btn);
        productImage=(ImageView)findViewById (R.id.product_image_details);
        productPrice=(TextView)findViewById (R.id.product_price_details);
        productDescription=(TextView)findViewById (R.id.product_description_details);
        productName=(TextView)findViewById (R.id.product_name_details);
        productID=getIntent ().getStringExtra ("pid");
        admin=getIntent ().getStringExtra ("admin");
       getProductDetails(productID);
       addToCartBtn.setOnClickListener (new View.OnClickListener () {
                                            @Override
                                            public void onClick(View v) {


                                                  addingToCartList();
                                            }
                                        }
       );
    }

    @Override
    protected void onStart() {
        super.onStart ();

    }

    private void addingToCartList() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat ("MMM dd,yyyy");
        saveCurrentDate=currentDate.format (calForDate.getTime ());
        SimpleDateFormat currentTime=new SimpleDateFormat ("HH:mm:ss a");
        saveCurrentTime=currentTime.format (calForDate.getTime ());
        final DatabaseReference cartListRef=FirebaseDatabase.getInstance ().getReference ().child ("Cart List");
        final HashMap<String,Object> cartMap=new HashMap<> ();
        cartMap.put("pid",productID);
        cartMap.put("admin",admin);
        cartMap.put("pname",productName.getText().toString ());
        cartMap.put("price",productPrice.getText().toString ());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("image","https://firebasestorage.googleapis.com/v0/b/ecommerce-c8c57.appspot.com/o/Product%20Images%2Fempty.png?alt=media&token=a0e3b881-cf18-4c4e-99eb-6a35a8a41216");
        cartMap.put("quantity",numberButton.getNumber ());
        cartMap.put("discount","0");
        cartListRef.child (Prevalent.currentOnlineUser.getPhone ())
                .child (productID)
                .updateChildren (cartMap)
                .addOnCompleteListener (new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText (getApplicationContext (),"Product Added To cart",Toast.LENGTH_LONG).show ();
                                        Intent it=new Intent(getApplicationContext (),HomeActivity.class);
                                        startActivity (it);
                                    }

                });
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef= FirebaseDatabase.getInstance ().getReference ().child ("Products");
        productsRef.child(productID).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    productName.setText (products.getPname ());
                    productPrice.setText(products.getPrice ());
                    productDescription.setText(products.getDescription ());
                    Picasso.get().load(products.getImage ()).into (productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
