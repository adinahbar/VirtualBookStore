package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class OpinionList extends AppCompatActivity {

    //this user
    Customer customer ;

    Book bookForTheOpinion;
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

    //function to set the list of opinions
    void initItemByListView(int bookIDOfOpinion)throws Exception
    {
        setListViewAsEmpty();
        //try to set items
        Backend backend = BackendFactory.getInstance();
        opinions = backend.getOpinionListOfBook(bookIDOfOpinion);
        opinionsItemForList = new ArrayList<>();
        for (Opinion o : opinions) {
            opinionsItemForList.add(new OpinionItemForList(o.getYourOpinion(),o.getRate(),o.getOpinionID()));
        }
        ArrayAdapter<OpinionItemForList> adapter = new ArrayAdapter<OpinionItemForList>(this,
                R.layout.row_of_opinion_item,opinionsItemForList)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(OpinionList.this, R.layout.row_of_opinion_item,null);
                }
                TextView opinionID = (TextView)convertView.findViewById(R.id.opinionIdTextView_row);
                TextView opinionRate = (TextView)convertView.findViewById(R.id.opinionRateTextView_row);
                TextView theOpinion = (TextView)convertView.findViewById(R.id.theOpinionTextView_row);
                opinionID.setText(String.valueOf(opinionsItemForList.get(position).getOpinionId()));
                theOpinion.setText(opinionsItemForList.get(position).getTheOpinion());
                opinionRate.setText(String.valueOf(opinionsItemForList.get(position).getRate()));
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_list);

        //this user
        Intent preIntent = getIntent();
        bookForTheOpinion = (Book) preIntent.getSerializableExtra(ConstValue.BOOK_KEY);
        customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //the list view
        listView = (ListView)findViewById(R.id.listViewOfAllOpinions);
        try {
            initItemByListView(bookForTheOpinion.getBookID());

        } catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(OpinionList.this, "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

}
