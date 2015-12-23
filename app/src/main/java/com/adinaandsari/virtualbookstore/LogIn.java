package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.privilege_spinner_log_in);
        String[] privilegeList = getResources().getStringArray(R.array.privilege_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,privilegeList);
        spinner.setAdapter(dataAdapter);

        //log in button
        Button button = (Button)findViewById(R.id.logInButton_log_in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the values
                EditText editTextOfID = (EditText)findViewById(R.id.enter_id_log_in);
                long id = Long.parseLong(editTextOfID.getText().toString());
                Spinner spinnerPrivilege = (Spinner)findViewById(R.id.privilege_spinner_log_in);
                String privilege = spinnerPrivilege.getSelectedItem().toString();
                Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                boolean found = false;
                //try to find the person and open the appropriate activity
                try {
                    switch (privilege) {
                        case "customer":
                            ArrayList<Customer> customerArrayList = backendFactory.getCustomerList(Privilege.MANAGER);
                            for (Customer c :customerArrayList){
                                if (c.getNumID()==id)
                                {
                                    found = true;
                                    Intent intent = new Intent(LogIn.this, CustomerActivity.class);
                                    intent.putExtra(ConstValue.CUSTOMER_KEY, c);//add the specific customer
                                    startActivity(intent);
                                }
                            }
                            if (!found)
                            {
                                throw new Exception("ERROR - The ID you entered doesn't match any existing customer");
                            }
                            break;
                        case "manager":
                            Manager manager = backendFactory.getManger();
                            if (manager.getNumID() == id)
                                found = true;
                            if (found)
                            {
                                Intent intent = new Intent(LogIn.this, ManagerActivity.class);
                                intent.putExtra(ConstValue.MANAGER_KEY, manager);//add the specific manager
                                startActivity(intent);
                            }
                            else
                                throw new Exception("ERROR - The ID you entered doesn't match any existing manger");
                            break;
                        case "supplier":
                            ArrayList<Supplier> supplierArrayList = backendFactory.getSupplierList();
                            for (Supplier s :supplierArrayList){
                                if (s.getNumID()==id)
                                {
                                    found = true;
                                    Intent intent = new Intent(LogIn.this, SupplierActivity.class);
                                    intent.putExtra(ConstValue.SUPPLIER_KEY, s);//add the specific supplier
                                    startActivity(intent);

                                }
                            }
                            if (!found)
                            {
                                throw new Exception("ERROR - The ID you entered doesn't match any existing supplier");
                            }
                            break;
                    }}
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(LogIn.this,"Failed to log in:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
