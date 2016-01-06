package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class RemoveOpinion extends AppCompatActivity {

    Manager user;
    private Spinner bookIdSpinner;
    private int bookID;
    ListView listView;
    private ArrayList<Opinion> opinions;
    private ArrayList<OpinionItemForList> opinionsItemForList;

    //function to set the list view as empty
    void setListViewAsEmpty()
    {
        TextView emptyView = new TextView(getApplicationContext());
        emptyView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.FILL_PARENT));
        emptyView.setText("No item in the list");
        emptyView.setTextSize(20);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    //function to set the list of orders
    void initItemByListView(int bookIDOfOpinion)throws Exception
    {
        setListViewAsEmpty();
        //try to set items
        Backend backend = BackendFactory.getInstance();
        opinions = backend.getOpinionListOfBook(bookID);
        opinionsItemForList = new ArrayList<>();
        /*
        for (Opinion o : opinions) {
            orderItemForLists.add(new OrderItemForList(o.getOrderID(), o.getTotalPrice(),name,o.getNumOfCopies() ));
        }
        ArrayAdapter<OrderItemForList> adapter = new ArrayAdapter<OrderItemForList>(this,
                R.layout.row_of_order_item,orderItemForLists)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(CustomerCart.this, R.layout.row_of_order_item,null);
                }
                TextView orderID = (TextView)convertView.findViewById(R.id.orderIdTextView_row);
                TextView bookName = (TextView)convertView.findViewById(R.id.bookNameOfOrderTextView_row);
                TextView price = (TextView)convertView.findViewById(R.id.orderPriceTextView_row);
                TextView numOfCopies = (TextView)convertView.findViewById(R.id.bookNumOfCopiesTextView_row);
                orderID.setText(String.valueOf(orderItemForLists.get(position).getOrderId()));
                bookName.setText(orderItemForLists.get(position).getBookName());
                price.setText(String.valueOf(orderItemForLists.get(position).getPrice()));
                numOfCopies.setText(String.valueOf(orderItemForLists.get(position).getNumOfCopies()));
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_opinion);
        //this user
        Intent preIntent = getIntent();
        user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);

        //the list view
        listView = (ListView)findViewById(R.id.listView_of_opinion_remove_opinion);

        //the book id spinner
        bookIdSpinner = (Spinner)findViewById(R.id.id_book_spinner_remove_opinion);
        //try to load the data of the selected book
        try {
            //spinner for the id of the book
            ArrayList<Book> books = BackendFactory.getInstance().getBookList();
            String[] ids = new String[books.size()];
            int i=0;
            for (Book b:books)
            {
                ids[i] = String.valueOf(b.getBookID());
                i++;
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,ids);
            bookIdSpinner.setAdapter(dataAdapter);
            bookIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    //set the list of opinion
                    bookID = Integer.valueOf((String)adapterView.getItemAtPosition(position));
                    try {
                        initItemByListView(bookID);

                    } catch (Exception e) {
                        //print the exception in a toast view
                        Toast.makeText(RemoveOpinion.this, "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(RemoveOpinion.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
