package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;
import java.util.List;


public class BookListFragment extends Fragment {
    public void setBookArrayList(ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
    }

    private ArrayList<Book> bookArrayList;
    private List<BookItemForList> bookItemForLists;

    public BookListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        bookItemForLists = new ArrayList<>();
        for(Book b : bookArrayList)
        {
            bookItemForLists.add(new BookItemForList(b.getBookName(),b.getBookID(),b.getAuthor()));
        }
        ListView listView = (ListView)getActivity().findViewById(R.id.listView__of_book);
        ArrayAdapter<BookItemForList> adapter = new ArrayAdapter<BookItemForList>(getActivity(),R.layout.row_of_book_item,bookItemForLists)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(getActivity(), R.layout.row_of_book_item,null);
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
        if (bookItemForLists.size()==0)//there are no books
        {
            TextView emptyView = new TextView(getActivity().getApplicationContext());
            emptyView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.FILL_PARENT));
            emptyView.setText("No item in the list");
            emptyView.setTextSize(20);
            emptyView.setVisibility(View.GONE);
            emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            ((ViewGroup)listView.getParent()).addView(emptyView);
            listView.setEmptyView(emptyView);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long id) {
                Intent intent = new Intent(getActivity(), BookPage.class);
                BookItemForList bookItemForList = (BookItemForList)adapterView.getItemAtPosition(position);
                try {
                    Book b = BackendFactory.getInstance().findBookByID(bookItemForList.getId());
                    intent.putExtra(ConstValue.BOOK_KEY, b);
                    Customer user = (Customer)getActivity().getIntent().getSerializableExtra(ConstValue.CUSTOMER_KEY);
                    intent.putExtra(ConstValue.CUSTOMER_KEY, user);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(getActivity(), "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_book, container, false);
        return view;
    }

}
