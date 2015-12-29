package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;

public class AddOrder extends AppCompatActivity {

    //this book
    Book bookToOrder;
    Customer user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //this book
        Intent preIntent = getIntent();
        bookToOrder = (Book) preIntent.getSerializableExtra(ConstValue.BOOK_KEY);
        //this customer
        user = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);
    }

}
