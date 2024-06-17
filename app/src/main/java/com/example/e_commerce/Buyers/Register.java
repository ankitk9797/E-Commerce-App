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

import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private Button createAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    private ProgressDialog loadingBar;
    private TextView isAdmin;
    String userType="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAccountButton=(Button)findViewById(R.id.register_btn);
        InputName=(EditText)findViewById(R.id.register_username_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        InputPhoneNumber=(EditText)findViewById(R.id.register_phone_number_input);
        isAdmin=(TextView)findViewById (R.id.isAdmin);
        isAdmin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(isAdmin.getText ().toString ().equals ("Admin?"))
                {
                    isAdmin.setText ("Buyer?");
                    userType="Admin";
                }
                else
                {
                    isAdmin.setText ("Seller?");
                    userType="Users";
                }
            }
        });
        loadingBar=new ProgressDialog(this);
          createAccountButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  createAccount ();
              }
          });
    }
    public void createAccount()
    {
        String name=InputName.getText().toString();
        String phone=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            Toast t=Toast.makeText(getApplicationContext(),"Please write your name...",Toast.LENGTH_LONG);
            t.show();
        }
        else if(TextUtils.isEmpty(phone))
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
          loadingBar.setTitle("Create Account");
          loadingBar.setMessage("Please Wait, while we are checking credentials...");
          loadingBar.setCanceledOnTouchOutside(false);
          loadingBar.show();
          validatePhoneNumber(name,phone,password);
        }
    }
    private void validatePhoneNumber(final String name, final String phone, final String password)
    {
       final DatabaseReference rootRef;
       rootRef= FirebaseDatabase.getInstance().getReference();
       rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(!dataSnapshot.child(userType).child(phone).exists())
               {
                 Map<String,Object> userdataMap=new HashMap<>();
                 userdataMap.put("phone",phone);
                 userdataMap.put("password",password);
                 userdataMap.put("name",name);
                 rootRef.child(userType).child(phone).updateChildren(userdataMap)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         Toast t=Toast.makeText(Register.this,"Congratulations, your account has been created ",Toast.LENGTH_LONG);
                                         t.show();
                                         loadingBar.dismiss();
                                         Intent intent=new Intent(Register.this,Login.class);
                                         startActivity(intent);
                                     }
                                     else
                                     {
                                         loadingBar.dismiss();
                                         Toast t=Toast.makeText(Register.this,"Network Error: try after some time",Toast.LENGTH_LONG);
                                         t.show();
                                     }
                             }
                         });
               }
               else
               {
                  Toast t1=Toast.makeText(getApplicationContext(),"This "+phone+" already exists",Toast.LENGTH_LONG);
                  t1.show();
                  loadingBar.dismiss();
                  Toast t2=Toast.makeText(getApplicationContext(),"Try again using another phone number",Toast.LENGTH_LONG);
                   t2.show();
                   Intent intent=new Intent(Register.this,MainActivity.class);
                   startActivity(intent);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
