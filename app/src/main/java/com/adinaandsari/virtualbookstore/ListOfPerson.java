package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adinaandsari.virtualbookstore.entities.Person;

import java.util.ArrayList;
import java.util.List;

public class ListOfPerson extends AppCompatActivity {

    private ArrayList<Person> personArrayList;
    private List<PersonItemForList>personItemForLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_person);

        Intent preIntent = getIntent();
        personArrayList = (ArrayList<Person>)preIntent.getSerializableExtra(ConstValue.PERSON_KEY);
        personItemForLists = new ArrayList<>();
        for(Person p : personArrayList)
        {
            personItemForLists.add(new PersonItemForList(p.getNumID(),p.getName(),p.getEmailAddress(),p.getPhoneNumber()));
        }
        ListView listView = (ListView)findViewById(R.id.listViewOfPerson);
        ArrayAdapter<PersonItemForList> adapter = new ArrayAdapter<PersonItemForList>
                (this,R.layout.row_of_person_item,personItemForLists)
        {
            @Override
            public View getView(int position,View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = View.inflate(ListOfPerson.this, R.layout.row_of_person_item,null);
                }
                TextView personID = (TextView)convertView.findViewById(R.id.personIdTextView_row);
                TextView personName = (TextView)convertView.findViewById(R.id.personNameTextView_row);
                TextView personPhoneNumber = (TextView)convertView.findViewById(R.id.personPhoneNumTextView_row);
                TextView personEmail = (TextView)convertView.findViewById(R.id.personEmailTextView_row);
                personID.setText(String.valueOf(personItemForLists.get(position).getPersonId()));
                personName.setText(personItemForLists.get(position).getName());
                personPhoneNumber.setText(personItemForLists.get(position).getPhoneNumber());
                personEmail.setText(personItemForLists.get(position).getEmail());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        this.setContentView(listView);
    }
}
