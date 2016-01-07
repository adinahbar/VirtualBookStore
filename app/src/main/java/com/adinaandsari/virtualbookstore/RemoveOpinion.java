package com.adinaandsari.virtualbookstore;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.adinaandsari.virtualbookstore.entities.Privilege;
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

    //function to set the list of opinion
    void initItemByListView(int bookIDOfOpinion)throws Exception
    {
        setListViewAsEmpty();
        //try to set items
        Backend backend = BackendFactory.getInstance();
        opinions = backend.getOpinionListOfBook(bookID);
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
                    convertView = View.inflate(RemoveOpinion.this, R.layout.row_of_opinion_item,null);
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

        //when press on the opinion to remove
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long id) {
                final OpinionItemForList opinionToRemove = (OpinionItemForList) adapterView.getItemAtPosition(position);
                new AlertDialog.Builder(RemoveOpinion.this)
                        .setTitle("Remove opinion")
                        .setMessage("Are you sure you want to remove this opinion? ")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    final Backend backend = BackendFactory.getInstance();
                                    final Opinion opinion = backend.findOpinionByID(opinionToRemove.opinionId);
                                    backend.removeOpinion(opinion.getOpinionID(),Privilege.MANAGER);
                                    //restart the activity
                                    Intent intent = new Intent(RemoveOpinion.this, ManagerActivity.class);
                                    intent.putExtra(ConstValue.MANAGER_KEY, user);//add the specific manager
                                    startActivity(intent);

                                } catch (Exception e) {
                                    //print the exception in a toast view
                                    Toast.makeText(RemoveOpinion.this, "Error:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

    }

}
