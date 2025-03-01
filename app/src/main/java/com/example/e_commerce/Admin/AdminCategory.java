package com.example.e_commerce.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.e_commerce.Buyers.HomeActivity;
import com.example.e_commerce.Buyers.MainActivity;
import com.example.e_commerce.R;

import io.paperdb.Paper;

public class AdminCategory extends AppCompatActivity {

    private ImageView tShirts,sportsTShirts,femaleDressese,sweathers;
    private ImageView glasses,hatsCaps,walletsBagsPurses,shoes;
    private ImageView headPhonesHandFree,Laptops,watches,mobilePhones;
    private Button LogoutBtn,CheckOrdersBtn,maintainProductsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_admin_category);
        LogoutBtn=(Button)findViewById (R.id.admin_logout_btn);
        CheckOrdersBtn=(Button)findViewById (R.id.check_orders_btn);
        maintainProductsBtn=(Button)findViewById (R.id.maintain_btn);
        maintainProductsBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent it=new Intent (getApplicationContext (), MyProducts.class);
                startActivity (it);
            }
        });
        LogoutBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Paper.book ().destroy ();
                Intent it=new Intent (getApplicationContext (), MainActivity.class);
                it.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (it);
                
                finish ();
            }
        });
        CheckOrdersBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent it=new Intent (getApplicationContext (),AdminNewOrdersActivity.class);
                startActivity (it);
            }
        });
        tShirts=(ImageView)findViewById (R.id.t_shirts);
        sportsTShirts=(ImageView)findViewById (R.id.sports_t_shirts);
        femaleDressese=(ImageView)findViewById (R.id.female_dresses);
        sweathers=(ImageView)findViewById (R.id.sweathers);
        glasses=(ImageView)findViewById (R.id.glasses);
        hatsCaps=(ImageView)findViewById (R.id.hats_caps);
        walletsBagsPurses=(ImageView)findViewById (R.id.purses_bags_wallets);
        shoes=(ImageView)findViewById (R.id.shoes);
        headPhonesHandFree=(ImageView)findViewById (R.id.headphone_headfree);
        Laptops=(ImageView)findViewById (R.id.laptops_pc);
        watches=(ImageView)findViewById (R.id.watches);
        mobilePhones=(ImageView)findViewById (R.id.mobilesphones);
        tShirts.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","tShirts");
                startActivity(intent);
            }
        }));
        sportsTShirts.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","SportsTShirts");
                startActivity(intent);
            }
        }));
        femaleDressese.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Female Dresses");
                startActivity(intent);
            }
        }));
        sweathers.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Sweathers");
                startActivity(intent);
            }
        }));
        glasses.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Glasses");
                startActivity(intent);
            }
        }));
        hatsCaps.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","HatsCaps");
                startActivity(intent);
            }
        }));
        walletsBagsPurses.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Wallets Bags Purses");
                startActivity(intent);
            }
        }));
        shoes.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Shoes");
                startActivity(intent);
            }
        }));
        headPhonesHandFree.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","HeadPhones HandFree");
                startActivity(intent);
            }
        }));
        Laptops.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Laptops");
                startActivity(intent);
            }
        }));
        watches.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Watches");
                startActivity(intent);
            }
        }));
        mobilePhones.setOnClickListener ((new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),AdminAddNewProduct.class);
                intent.putExtra ("category","Mobile Phones");
                startActivity(intent);
            }
        }));
    }
}
