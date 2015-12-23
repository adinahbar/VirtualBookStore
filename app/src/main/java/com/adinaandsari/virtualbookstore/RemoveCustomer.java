package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class RemoveCustomer extends AppCompatActivity {

    //this manager is entering the activity
    Intent preIntent = getIntent();
    Manager user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);
    private Spinner removeCustomerIdSpinner;
    private Long idToRemove;
    private Button removeCustomerButton;
    void showDetail()throws Exception
    {
        ArrayList<Customer> customers = BackendFactory.getInstance().getCustomerList(user.getPrivilege());
        String[] ids = new String[customers.size()];
        int i=0;
        for (Customer c:customers)
        {
            ids[i] = String.valueOf(c.getNumID());
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,ids);
        removeCustomerIdSpinner.setAdapter(dataAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        removeCustomerIdSpinner = (Spinner)findViewById(R.id.select_customer_to_remove__by_ID_spinner);
        removeCustomerButton = (Button)findViewById(R.id.remove_customer_button);
        try {
            showDetail();
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(RemoveCustomer.this, "Failed to show the customer's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        removeCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    idToRemove = Long.valueOf(removeCustomerIdSpinner.getSelectedItem().toString());//getting selected id
                    BackendFactory.getInstance().removeCustomer(idToRemove, user.getPrivilege());
                    Toast.makeText(RemoveCustomer.this, "the selected customer was removed successfully:\n" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RemoveCustomer.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, user);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(RemoveCustomer.this, "Failed to remove customer:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }


        });

    }
}
