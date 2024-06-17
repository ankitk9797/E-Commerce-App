package com.example.e_commerce.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProduct extends AppCompatActivity {

    private String categoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button AddnNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProductDescription,InputProductPrice;
    private static final int galleryPick=1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef,MyProductsRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_admin_add_new_product);
           categoryName=getIntent ().getExtras ().getString ("category");
         ProductImagesRef= FirebaseStorage.getInstance ().getReference ().child ("Product Images");
         ProductsRef=FirebaseDatabase.getInstance ().getReference ().child ("Products");
        MyProductsRef=FirebaseDatabase.getInstance ().getReference ().child ("MyProducts");
         AddnNewProductButton=(Button)findViewById (R.id.add_new_product);
         InputProductImage=(ImageView)findViewById (R.id.select_product_image);
         InputProductName=(EditText)findViewById (R.id.product_name);
         InputProductDescription=(EditText)findViewById (R.id.product_description);
         loadingBar=new ProgressDialog (this);
         InputProductPrice=(EditText)findViewById (R.id.product_price);
         InputProductImage.setOnClickListener (new View.OnClickListener () {
             @Override
             public void onClick(View v) {
                 openGallery();
             }
         });
         AddnNewProductButton.setOnClickListener (new View.OnClickListener () {
             @Override
             public void onClick(View v) {
                 ValidateProductData();
             }
         });
    }

    private void ValidateProductData() {
        Description=InputProductDescription.getText().toString ();
        Price=InputProductPrice.getText ().toString ();
        Pname=InputProductName.getText ().toString ();
        if(ImageUri==null)
        {
            Toast.makeText (getApplicationContext (),"Product image is mandatory...",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (Description))
        {
           Toast.makeText (getApplicationContext (),"Please write product description",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (Price))
        {
            Toast.makeText (getApplicationContext (),"Please write product price",Toast.LENGTH_LONG).show ();
        }
        else if(TextUtils.isEmpty (Pname))
        {
            Toast.makeText (getApplicationContext (),"Please write product name",Toast.LENGTH_LONG).show ();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please Wait, while we are adding new product...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat ("MM dd,yyyy");
        saveCurrentDate=currentDate.format (calendar.getTime ());
        SimpleDateFormat currentTime=new SimpleDateFormat ("HH:mm:ss a");
        saveCurrentTime=currentTime.format (calendar.getTime ());
        productRandomKey=saveCurrentDate+saveCurrentTime;
        final StorageReference filePath =ProductImagesRef.child(ImageUri.getLastPathSegment ()+productRandomKey+".jpg");
        final UploadTask uploadTask=filePath.putFile (ImageUri);
        uploadTask.addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString ();
                Toast.makeText (getApplicationContext (),"Error:"+e,Toast.LENGTH_LONG).show ();
                loadingBar.dismiss ();
            }
        }).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask=uploadTask.continueWithTask (new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful ())
                        {
                            throw task.getException ();

                        }
                        downloadImageUrl=filePath.getDownloadUrl ().toString ();
                        return filePath.getDownloadUrl ();
                    }
                }).addOnCompleteListener (new OnCompleteListener<Uri> () {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful ()) {
                            downloadImageUrl=task.getResult ().toString ();
                        saveProductInfoToDatabase();
                        }
                        }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<> ();
        productMap.put("Admin",Prevalent.currentOnlineUser.getPhone ());
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);
        ProductsRef.child (productRandomKey).updateChildren (productMap).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful ())
                {
                    HashMap<String,Object> myproductMap=new HashMap<> ();
                    myproductMap.put("pid",productRandomKey);
                    MyProductsRef.child (Prevalent.currentOnlineUser.getPhone ()).child(productRandomKey).updateChildren (myproductMap);
                    Intent intent=new Intent(getApplicationContext (),AdminCategory.class);
                    startActivity (intent);
                    loadingBar.dismiss ();
                    Toast.makeText (getApplicationContext (),"Product is added successfully",Toast.LENGTH_LONG).show ();
                }
                else
                {
                    loadingBar.dismiss ();
                    String message=task.getException ().toString ();
                    Toast.makeText (getApplicationContext (),"Error:"+message,Toast.LENGTH_LONG).show ();
                }
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction (Intent.ACTION_GET_CONTENT);
        galleryIntent.setType ("image/*");
        startActivityForResult (galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData ();
            InputProductImage.setImageURI (ImageUri);
        }
    }
}
