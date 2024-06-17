package com.example.e_commerce.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Admin.AdminCategory;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;

import com.example.e_commerce.model.users;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton,loginButton;
    private  String parentDBName="Users";
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButton=(Button)findViewById(R.id.main_join_now_btn);
        loginButton=(Button)findViewById(R.id.main_login_btn);
        Paper.init (this);
        loadingBar=new ProgressDialog (this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!="" && UserPasswordKey!="")
        {
            if(!TextUtils.isEmpty (UserPhoneKey) && !TextUtils.isEmpty (UserPasswordKey))
            {
                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please Wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                String type=Paper.book().read(Prevalent.UserType);
                AllowAccess(UserPhoneKey,UserPasswordKey,type);
            }
        }
    }


    private void AllowAccess(final String phone, final String password,final String type) {
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBName).child(phone).exists())
                {
                    users usersData=dataSnapshot.child(parentDBName).child(phone).getValue (users.class);
                    if(usersData.getPhone ().equals (phone))
                    {
                        if(usersData.getPassword ().equals (password))
                        {
                            Toast t=Toast.makeText (getApplicationContext (),"Logged in Successfully...",Toast.LENGTH_LONG);
                            t.show();
                            Prevalent.currentOnlineUser=usersData;
                            if(type.equals ("Admin")) {
                                Intent intent = new Intent (getApplicationContext (), AdminCategory.class);
                                startActivity (intent);
                            }
                                else {
                                Intent intent = new Intent (getApplicationContext (), HomeActivity.class);
                                startActivity (intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss ();
                            Toast t=Toast.makeText (getApplicationContext (),"Incorrect Password",Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                }
                else
                {
                    Toast t=Toast.makeText(getApplicationContext (),"Account with this "+phone+" not exists",Toast.LENGTH_LONG);
                    t.show ();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
