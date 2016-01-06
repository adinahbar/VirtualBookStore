package com.adinaandsari.virtualbookstore;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_by_category);

        //the user
        Intent preIntent = getIntent();
        customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //the category spinner
        categorySpinner = (Spinner) findViewById(R.id.category_spinner_shop_by_category);
        String[] oldCategoryList = getResources().getStringArray(R.array.category_array);
        String[] categoryList = new String[oldCategoryList.length + 1];
        int i=1;
        categoryList[0] = "No Category was selected";
        for(String s:oldCategoryList)
        {
            categoryList[i] = s;
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categoryList);
        categorySpinner.setAdapter(dataAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //the first time
                String chosenCategory = (String)adapterView.getItemAtPosition(position);
                if (chosenCategory.equals("No Category was selected"))
                {
                    FragmentManager fragmentManager = getFragmentManager();
                    BlankFragment fragment = new BlankFragment();
                    fragmentManager.beginTransaction().replace(R.id.bookList_frame_layout_shop_by_category, fragment).commit();
                }
                else//for a chosen category
                {
                    Category category = Category.valueOf(chosenCategory.toUpperCase());
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    try {
                        ArrayList<Book> books = backendFactory.bookListSortedByCategory(category);
                        FragmentManager fragmentManager = getFragmentManager();
                        BookListFragment fragment = new BookListFragment();
                        fragment.setBookArrayList(books);
                        fragmentManager.beginTransaction().replace(R.id.bookList_frame_layout_shop_by_category, fragment).commit();
                    } catch (Exception e) {
                        Toast.makeText(ShopByCategoryActivity.this, "There are no books for now", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
