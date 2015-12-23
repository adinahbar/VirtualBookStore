package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.adinaandsari.virtualbookstore.entities.Supplier;

public class SupplierActivity extends AppCompatActivity {

    //this supplier
    Intent preIntent = getIntent();
    Supplier user = (Supplier) preIntent.getSerializableExtra(ConstValue.SUPPLIER_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //invite text view
        TextView inviteTextView = (TextView) findViewById(R.id.invite_theSupplier_textView_supplier);
        String text = "Dear " + user.getName() +
                "\nWe invite you to increase your cooperation with us!";
        inviteTextView.setText(text);
    }

}
