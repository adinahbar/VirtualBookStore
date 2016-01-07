package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;
import java.util.List;

public class BookListForSupplier extends AppCompatActivity {

    private ArrayList<Book> bookArrayList;
    private List<BookItemForList> bookItemForLists;
    ListView listView;
    //this user
    Supplier supplier ;

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

    //function to set the list of books
    void initItemByListView()throws Exception
    {
        setListViewAsEmpty();
        //try to set items

        Backend backend = BackendFactory.getInstance();
        bookArrayList = backend.bookListBySupplier(supplier.getNumID());
        bookItemForLists = new ArrayList<>();
        for(Book b : bookArrayList)
        {
            bookItemForLists.add(new BookItemForList(b.getBookName(),b.getBookID(),b.getAuthor()));
        }
        ArrayAdapter<BookItemForList> adapter = new ArrayAdapter<BookItemForList>(this,R.layout.row_of_book_item,bookItemForLists)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(BookListForSupplier.this, R.layout.row_of_book_item,null);
                }
                TextView bookID = (TextView)convertView.findViewById(R.id.bookIdTextView_row);
                TextView bookName = (TextView)convertView.findViewById(R.id.bookTitleTextVieww_row);
                TextView bookAuthor = (TextView)convertView.findViewById(R.id.bookAuthorTextVieww_row);
                bookID.setText(String.valueOf(bookItemForLists.get(position).getId()));
                bookName.setText(bookItemForLists.get(position).getName());
                bookAuthor.setText(bookItemForLists.get(position).getAuthor());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list_for_supplier);

        //the user
        Intent preIntent = getIntent();
        supplier = (Supplier)preIntent.getSerializableExtra(ConstValue.SUPPLIER_KEY);

        //the list view
        listView = (ListView)findViewById(R.id.listViewOfBookForSupplier);
        try {
            initItemByListView();

        } catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(BookListForSupplier.this, "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

}
