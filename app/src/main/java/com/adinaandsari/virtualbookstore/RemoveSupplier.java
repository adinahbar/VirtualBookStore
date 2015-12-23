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

import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Status;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class RemoveSupplier extends AppCompatActivity {

    //this manager is entering the activity
    Intent preIntent = getIntent();
    Manager user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);
    private Spinner removeSupplierIdSpinner;
    private Long idToRemove;
    private Button removeSupplierButton;
    void showDetail()throws Exception
    {
        ArrayList<Supplier> suppliers = BackendFactory.getInstance().getSupplierList();
        String[] ids = new String[suppliers.size()];
        int i=0;
        for (Supplier s:suppliers)
        {
            ids[i] = String.valueOf(s.getNumID());
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,ids);
        removeSupplierIdSpinner.setAdapter(dataAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_remove_supplier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        removeSupplierIdSpinner = (Spinner)findViewById(R.id.select_supplier_to_remove__by_ID_spinner);
        removeSupplierButton = (Button)findViewById(R.id.remove_supplier_button);
        try {
            showDetail();
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(RemoveSupplier.this, "Failed to show the supplier's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        removeSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    idToRemove = Long.valueOf(removeSupplierIdSpinner.getSelectedItem().toString());//getting selected id
                    BackendFactory.getInstance().removeSupplier(idToRemove,user.getPrivilege());
                    Toast.makeText(RemoveSupplier.this, "the selected supplier was removed successfully:\n" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RemoveSupplier.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, user);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(RemoveSupplier.this, "Failed to remove supplier:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }


        });

    }
}