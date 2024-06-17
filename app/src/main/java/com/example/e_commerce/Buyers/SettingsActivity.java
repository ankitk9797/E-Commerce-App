package com.example.e_commerce.Buyers;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText,userPhoneEditText,addressEdiText;
    private TextView profileChangeTextBtn,CloseTextBtn,saveTextBtn;
   // private Button securityQuestions;
    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_settings);
        profileImageView=(CircleImageView)findViewById (R.id.settings_profile_image);
        fullNameEditText=(EditText)findViewById (R.id.settings_full_name);
        userPhoneEditText=(EditText)findViewById (R.id.settings_phone_number);
        addressEdiText=(EditText)findViewById (R.id.settings_address);
        profileChangeTextBtn=(TextView)findViewById (R.id.profile_image_change_btn);
        CloseTextBtn=(TextView)findViewById (R.id.close_settings_btn);
        saveTextBtn=(TextView)findViewById (R.id.update_account_settings_btn);
       // securityQuestions=(Button)findViewById (R.id.security_questions_btn);
         storageProfilePictureRef= FirebaseStorage.getInstance ().getReference ("Profile pictures");
        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEdiText);
       CloseTextBtn.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
       saveTextBtn.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               if(checker.equals ("clicked"))
               {
                   userInfoSaved();
               }
               else
               {
                   updateOnlyUserInfo();
               }
           }
       });
       profileChangeTextBtn.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               checker="clicked";
               CropImage.activity(imageUri)
                       .setAspectRatio (1,1)
                       .start(SettingsActivity.this);
           }
       });
       /*securityQuestions.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               Intent it=new Intent (getApplicationContext (),ResetPasswordActivity.class);
               it.putExtra ("check","settings");
               startActivity (it);
           }
       });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult (data);
               imageUri=result.getUri ();
               profileImageView.setImageURI (imageUri);
        }
        else
        {
            Toast.makeText (getApplicationContext (),"Error, Try Again",Toast.LENGTH_LONG).show ();
            startActivity (new Intent (getApplicationContext (),SettingsActivity.class));
            finish();
        }


    }

    private void userInfoSaved() {
         if(TextUtils.isEmpty (fullNameEditText.getText ().toString ()))
         {
             Toast.makeText (getApplicationContext (),"Name is mandatory",Toast.LENGTH_LONG);
         }
         else if(TextUtils.isEmpty (addressEdiText.getText ().toString ()))
         {
             Toast.makeText (getApplicationContext (),"Address is mandatory",Toast.LENGTH_LONG);
         }
         else if(TextUtils.isEmpty (userPhoneEditText.getText ().toString ()))
         {
             Toast.makeText (getApplicationContext (),"Phone is mandatory",Toast.LENGTH_LONG);
         }
         else if(checker.equals ("clicked"))
         {
             uploadImage();
         }
    }
  private void uploadImage()
  {
      final ProgressDialog progressDialog=new ProgressDialog (this);
      progressDialog.setTitle ("Update Profile");
      progressDialog.setMessage("Please wait, while we are updating your account information");
      progressDialog.setCanceledOnTouchOutside (false);
      progressDialog.show ();
      if(imageUri!=null)
      {
          final StorageReference fileRef=storageProfilePictureRef
                  .child (Prevalent.currentOnlineUser.getPhone ()+".jpg");
          uploadTask=fileRef.putFile (imageUri);
          uploadTask.continueWithTask (new Continuation () {
              @Override
              public Object then(@NonNull Task task) throws Exception {
                 if(!task.isSuccessful ())
                     throw  task.getException ();
                 return fileRef.getDownloadUrl ();
              }
          }).addOnCompleteListener (new OnCompleteListener<Uri> () {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                  if(task.isSuccessful ())
                  {
                      Uri downloadUrl=task.getResult ();
                      myUrl=downloadUrl.toString ();
                      DatabaseReference ref=FirebaseDatabase.getInstance ().getReference ().child ("Users");
                      HashMap<String,Object> userMap=new HashMap<> ();
                      userMap.put("name",fullNameEditText.getText ().toString ());
                      userMap.put("address",addressEdiText.getText ().toString ());
                      //userMap.put("phoneOrder",userPhoneEditText.getText ().toString ());
                      userMap.put("image",myUrl);
                      ref.child (Prevalent.currentOnlineUser.getPhone ()).updateChildren (userMap);
                      progressDialog.dismiss ();
                      startActivity (new Intent (getApplicationContext (),HomeActivity.class));
                      Toast.makeText (getApplicationContext (),"Profile Info update successfully",Toast.LENGTH_LONG).show ();
                      finish ();
                  }
                  else
                  {
                    progressDialog.dismiss ();
                    Toast.makeText (getApplicationContext (),"Error",Toast.LENGTH_LONG).show ();
                  }
              }
          });
      }
      else
      {
          Toast.makeText (getApplicationContext (),"Image is not selected",Toast.LENGTH_LONG).show ();
      }
  }
    private void updateOnlyUserInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance ().getReference ().child ("Users");
        HashMap<String,Object> userMap=new HashMap<> ();
        userMap.put("name",fullNameEditText.getText ().toString ());
        userMap.put("address",addressEdiText.getText ().toString ());
        //userMap.put("phoneOrder",userPhoneEditText.getText ().toString ());
        ref.child (Prevalent.currentOnlineUser.getPhone ()).updateChildren (userMap);
        startActivity (new Intent (getApplicationContext (),HomeActivity.class));
        Toast.makeText (getApplicationContext (),"Profile Info update successfully",Toast.LENGTH_LONG).show ();
        finish ();
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEdiText) {
        DatabaseReference UsersRef= FirebaseDatabase.getInstance ().getReference ().child ("Users").child (Prevalent.currentOnlineUser.getPhone ());
       UsersRef.addValueEventListener (new ValueEventListener () {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists ())
                    {
                        String image=dataSnapshot.child("image").getValue ().toString ();
                        String name=dataSnapshot.child("name").getValue ().toString ();
                        String phone=dataSnapshot.child("phone").getValue ().toString ();
                        String address=dataSnapshot.child("address").getValue ().toString ();
                        Picasso.get ().load (image).into (profileImageView);
                        fullNameEditText.setText (name);
                        userPhoneEditText.setText (phone);
                        addressEdiText.setText (address);
                    }
                }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }
}
