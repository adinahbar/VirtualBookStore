package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierType;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;
import java.util.Locale;

public class UpdateSupplier extends AppCompatActivity {

    //this user
    Manager user ;

    private long id;
    private String name ,address , phoneNumber , email , customerServiceNum,reservationNum;
    private Gender gender;
    private SupplierType supplierType;
    private Spinner idSpinner , supplierTypeSpinner;
    EditText editTextOfName,editTextOfAddress,editTextOfPhoneNumber,editTextOfEmail,editTextOfCustomerServiceNum,editTextOfReservationNum;
    private CheckBox male,female;
    Button updateButton;

    //function to find view by id for the view in the activity
    void findView()
    {
        editTextOfName = (EditText)findViewById(R.id.name_edit_text_update_supplier);
        editTextOfAddress = (EditText)findViewById(R.id.address_edit_text_update_supplier);
        editTextOfPhoneNumber = (EditText)findViewById(R.id.phone_number_edit_text_update_supplier);
        editTextOfEmail = (EditText)findViewById(R.id.email_edit_text_update_supplier);
        editTextOfCustomerServiceNum = (EditText)findViewById(R.id.customer_service_num_edit_text_update_supplier);
        editTextOfReservationNum = (EditText)findViewById(R.id.reservation_num_edit_text_update_supplier);
        idSpinner = (Spinner)findViewById(R.id.id_spinner_update_supplier);
        supplierTypeSpinner = (Spinner)findViewById(R.id.type_spinner_update_supplier);
        male = (CheckBox) findViewById(R.id.male_checkBox_update_supplier);
        female = (CheckBox) findViewById(R.id.female_checkBox_update_supplier);
        updateButton = (Button)findViewById(R.id.update_supplier_button_update_supplier);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_supplier);
        //this user
        Intent preIntent = getIntent();
        user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);
        //find view
        findView();
        //spinner for the type of the supplier
        String[] typeList = getResources().getStringArray(R.array.supplier_type_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,typeList);
        supplierTypeSpinner.setAdapter(dataAdapter);
        //try to load the data of the selected supplier
        try {
            //spinner for the id of the supplier
            String[] supplierID = getSupplierID();
            ArrayAdapter<String> dataIDAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,supplierID);
            idSpinner.setAdapter(dataIDAdapter);
            idSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    enterDetail();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(UpdateSupplier.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        //update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    id = Long.parseLong(idSpinner.getSelectedItem().toString());
                    name = editTextOfName.getText().toString();
                    address = editTextOfAddress.getText().toString();
                    phoneNumber = editTextOfPhoneNumber.getText().toString();
                    if (phoneNumber.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: Phone number must contain only digits");
                    email = editTextOfEmail.getText().toString();
                    if (email.contains("@") == false) {
                        throw new Exception("ERROR: your email address is wrong");
                    }
                    customerServiceNum = editTextOfCustomerServiceNum.getText().toString();
                    if (customerServiceNum.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: customer reservation number must contain only digits");
                    reservationNum = editTextOfReservationNum.getText().toString();
                    if (reservationNum.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR:reservation number must contain only digits");
                    supplierType = SupplierType.valueOf(supplierTypeSpinner.getSelectedItem().toString().toUpperCase());
                    if (male.isChecked()) {
                        gender = Gender.MALE;
                    } else if (female.isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    //try to update the supplier
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Supplier supplierToUpdate = new Supplier(id, name, address, phoneNumber, email, gender,
                            customerServiceNum, reservationNum, supplierType);
                    backendFactory.updateSupplier(supplierToUpdate, user.getPrivilege());
                    Toast.makeText(UpdateSupplier.this, "The update has been successful!", Toast.LENGTH_LONG).show();

                    //go back to the activity
                    Intent intent = new Intent(UpdateSupplier.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, user);
                    startActivity(intent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(UpdateSupplier.this, "Failed to update the supplier:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //function to enter the values of the selected supplier by its ID
    void enterDetail()
    {
        try {
            Supplier supplier = BackendFactory.getInstance().findSupplierByID(Long.parseLong(idSpinner.getSelectedItem().toString()));
            editTextOfName.setText(supplier.getName());
            editTextOfAddress.setText(supplier.getAddress());
            editTextOfPhoneNumber.setText(supplier.getPhoneNumber());
            editTextOfEmail.setText(supplier.getEmailAddress());
            editTextOfCustomerServiceNum.setText(supplier.getCustomerServicePhoneNumber());
            editTextOfReservationNum.setText(supplier.getReservationsPhoneNumber());
            switch (supplier.getGender())
            {
                case MALE:
                    male.setChecked(true);
                    female.setChecked(false);
                    break;
                case FEMALE:
                    female.setChecked(true);
                    male.setChecked(false);
                    break;
            }
            String typeString = supplier.getType().toString().toLowerCase(Locale.ENGLISH);
            supplierTypeSpinner.setSelection(((ArrayAdapter<String>) supplierTypeSpinner.getAdapter()).getPosition(typeString));
        }
        catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(UpdateSupplier.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //function to return all the suppliers id
    String[] getSupplierID ()throws Exception
    {
        ArrayList<Supplier> supplierArrayList = BackendFactory.getInstance().getSupplierList();
        String[] id = new String[ supplierArrayList.size()] ;
        int i = 0;
        for (Supplier s : supplierArrayList)
        {
            id[i] =((Long)s.getNumID()).toString();
            i++;
        }
        return id;
    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_update_supplier:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_update_supplier:
                male.setChecked(false);
        }
    }
}
