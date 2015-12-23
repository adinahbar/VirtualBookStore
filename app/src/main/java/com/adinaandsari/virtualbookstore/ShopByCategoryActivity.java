package com.adinaandsari.virtualbookstore;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import java.util.ArrayList;

public class ShopByCategoryActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    Intent preIntent = getIntent();
    Customer customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_by_category);

        //the category spinner
        categorySpinner = (Spinner) findViewById(R.id.category_spinner_shop_by_category);
        String[] categoryList = getResources().getStringArray(R.array.category_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,categoryList);
        categorySpinner.setAdapter(dataAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = Category.valueOf(categorySpinner.getSelectedItem().toString().toUpperCase());
                Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                try {
                    ArrayList<Book> books = backendFactory.bookListSortedByCategory(category);
                    getIntent().putExtra("bookList",books);
                    FragmentManager fragmentManager = getFragmentManager();
                    BookListFragment fragment = new BookListFragment();
                    fragmentManager.beginTransaction().replace(R.id.bookList_frame_layout,fragment).commit();
                }catch (Exception e)
                {
                    Toast.makeText(ShopByCategoryActivity.this, "There are no books for now", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
