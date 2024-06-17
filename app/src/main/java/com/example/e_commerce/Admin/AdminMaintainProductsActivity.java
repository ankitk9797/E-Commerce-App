package com.example.e_commerce.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_admin_maintain_products);
        applyChangesBtn=(Button)findViewById (R.id.apply_changes_btn);
        name=(EditText)findViewById (R.id.product_name_maintain);
        price=(EditText)findViewById (R.id.product_price_maintain);
        description=(EditText)findViewById (R.id.product_description_maintain);
        imageView=(ImageView)findViewById (R.id.product_image_maintain);
        deleteBtn=(Button)findViewById (R.id.delete_products_btn); 
        productID=getIntent ().getStringExtra ("pid");
        productsRef= FirebaseDatabase.getInstance ().getReference ().child ("Products").child (productID);
     displaySpecificProductInfo();
     applyChangesBtn.setOnClickListener (new View.OnClickListener () {
         @Override
         public void onClick(View v) {
             applyChanges();
         }
     });
     deleteBtn.setOnClickListener (new View.OnClickListener () {
         @Override
         public void onClick(View v) {
             deleteThisProduct();
         }
     });
    }

    private void deleteThisProduct() {
        productsRef.removeValue ().addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent it=new Intent (getApplicationContext (),AdminCategory.class);
                startActivity (it);
                finish ();
             Toast.makeText (getApplicationContext (),"The Product is Deleted successfully",Toast.LENGTH_LONG).show ();
            }
        });
    }

    private void applyChanges() {
        String pName=name.getText().toString ();
        String pPrice=price.getText ().toString ();
        String pDescription=description.getText ().toString ();
        if(pName.equals (""))
            Toast.makeText (getApplicationContext (),"Please write Product Name.",Toast.LENGTH_LONG).show ();
        else if(pPrice.equals (""))
            Toast.makeText (getApplicationContext (),"Please write Product Price.",Toast.LENGTH_LONG).show ();
        else if(pDescription.equals (""))
            Toast.makeText (getApplicationContext (),"Please write Product Description.",Toast.LENGTH_LONG).show ();
        else
        {
            HashMap<String,Object> productMap=new HashMap<> ();
            productMap.put("pid",productID);
            productMap.put("description",pDescription);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);
            productsRef.updateChildren (productMap).addOnCompleteListener (new OnCompleteListener<Void> () {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful ())
                 {
                     Toast.makeText (getApplicationContext (),"Changes Applied successfully.",Toast.LENGTH_LONG).show ();
                     Intent it=new Intent (getApplicationContext (),AdminCategory.class);
                     startActivity (it);
                     finish ();
                 }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ())
                {
                    String pName=dataSnapshot.child ("pname").getValue ().toString ();
                    String pPrice=dataSnapshot.child ("price").getValue ().toString ();
                    String pDescription=dataSnapshot.child ("description").getValue ().toString ();
                    String pImage=dataSnapshot.child ("image").getValue ().toString ();
                   name.setText (pName);
                   price.setText (pPrice);
                   description.setText (pDescription);
                    Picasso.get ().load (pImage).into (imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
