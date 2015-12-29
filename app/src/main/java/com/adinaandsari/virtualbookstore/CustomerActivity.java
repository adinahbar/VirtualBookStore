package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

public class CustomerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    //this customer
    Customer user;

    Backend backendFactory = BackendFactory.getInstance();
    private Book bookOfTheStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent preIntent = getIntent();
        user = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_customer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_customer);
        navigationView.setNavigationItemSelectedListener(this);

        //the name text view
        TextView name = (TextView) findViewById(R.id.name_of_customer_textView_customer);
        String text = "Dear " + user.getName() +
             "\nWe invite you to see our special books!!";
        name.setText(text);

        //the book of the store
        TextView bookOfTheStoreText = (TextView) findViewById(R.id.book_of_the_store_textView);
        try {
            bookOfTheStore = backendFactory.bookOfTheStore();
            bookOfTheStoreText.setText(bookOfTheStore.getBookName());
        } catch (Exception e) {
            bookOfTheStoreText.setText("There are no best seller book to watch");
        }
        Button shopBookOfTheStoreButton = (Button)findViewById(R.id.shop_book_of_the_store_button_customer);
        shopBookOfTheStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookOfTheStore != null) {
                    //go to the order activity for this book and customer
                    Intent intent = new Intent(CustomerActivity.this, BookPage.class);
                    intent.putExtra(ConstValue.BOOK_KEY, bookOfTheStore);//add the book
                    intent.putExtra(ConstValue.CUSTOMER_KEY, user);//add the customer
                    startActivity(intent);
                }
                Toast.makeText(CustomerActivity.this, "There are no book of the store for now", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_customer);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.myCart:
                intent = new Intent(CustomerActivity.this, CustomerCart.class);
                break;
            case R.id.shopAllBook:
                intent = new Intent(CustomerActivity.this, ShopAllBook.class);
                break;
            case R.id.shopByCategory:
                intent = new Intent(CustomerActivity.this, ShopByCategoryActivity.class);
                break;
            case R.id.shopByAuthors:
                intent = new Intent(CustomerActivity.this, ShopByAuthorActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.putExtra(ConstValue.CUSTOMER_KEY, user);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_customer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
