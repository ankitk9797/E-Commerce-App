package com.example.e_commerce.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.rey.material.widget.CheckBox;
import io.paperdb.Paper;


public class Login extends AppCompatActivity {

    private EditText InputPhoneNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDBName="Users";
    private CheckBox chkboxRememberMe;
    private TextView AdminLink,NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton=(Button)findViewById(R.id.login_btn);
        InputPassword=(EditText)findViewById(R.id.login_password_input);
        InputPhoneNumber=(EditText)findViewById(R.id.login_phone_number_input);

        Paper.init(this);
        AdminLink=(TextView)findViewById (R.id.admin_panel_link);
        NotAdminLink=(TextView)findViewById (R.id.not_admin_panel_link);
        loadingBar=new ProgressDialog(this);
        chkboxRememberMe=(CheckBox)findViewById (R.id.remember_me_checkbox);
        LoginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        AdminLink.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                LoginButton.setText ("Login Seller");
                AdminLink.setVisibility (View.INVISIBLE);
                NotAdminLink.setVisibility (View.VISIBLE);
                parentDBName="Admin";
            }
        });
        NotAdminLink.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Buyer");
                AdminLink.setVisibility (View.VISIBLE);
                NotAdminLink.setVisibility (View.INVISIBLE);
                parentDBName="Users";
            }
        });

    }
    public void loginUser()
    {
        String phone=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();
        if(TextUtils.isEmpty(phone))
        {
            Toast t=Toast.makeText(getApplicationContext(),"Please write your phone number...",Toast.LENGTH_LONG);
            t.show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast t=Toast.makeText(getApplicationContext(),"Please write your password...",Toast.LENGTH_LONG);
            t.show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please Wait, while we are checking credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phone,password);
        }
    }
    private void AllowAccessToAccount(final String phone,final String password)
    {
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
                              if(chkboxRememberMe.isChecked ())
                              {
                                  Paper.book().write(Prevalent.UserPhoneKey,phone);
                                  Paper.book().write(Prevalent.UserPasswordKey,password);
                                  Paper.book().write(Prevalent.UserType,parentDBName);
                              }
                             if(parentDBName.equals ("Admin"))
                             {
                                 Toast t=Toast.makeText (getApplicationContext (),"Welcome Admin...",Toast.LENGTH_LONG);
                                 t.show();
                                 Prevalent.currentOnlineUser=usersData;
                                 Intent intent=new Intent (getApplicationContext (), AdminCategory.class);
                                 startActivity (intent);
                             }
                             else if(parentDBName.equals ("Users"))
                              {
                                  Toast t=Toast.makeText (getApplicationContext (),"Logged in Successfully...",Toast.LENGTH_LONG);
                                  t.show();
                                  Intent intent=new Intent (getApplicationContext (),HomeActivity.class);
                                  Prevalent.currentOnlineUser=usersData;
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
