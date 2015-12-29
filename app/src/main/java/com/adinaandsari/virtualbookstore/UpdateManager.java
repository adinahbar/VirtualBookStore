package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

public class UpdateManager extends AppCompatActivity {

    //this user
    Manager user;

    private long id;
    private String name ,address , phoneNumber , email;
    private Gender gender;
    private int yearsInCompany;
    TextView textViewOfID;
    EditText editTextOfName,editTextOfAddress,editTextOfPhoneNumber,editTextOfEmail,editTextOfYearsInCompany;
    private CheckBox male,female;
    Button updateButton;

    //function to find view by id for the view in the activity
    void findView()
    {
        textViewOfID = (TextView)findViewById(R.id.id_of_manager_text_view_update_manager);
        editTextOfName = (EditText)findViewById(R.id.name_edit_text_update_manager);
        editTextOfAddress = (EditText)findViewById(R.id.address_edit_text_update_manager);
        editTextOfPhoneNumber = (EditText)findViewById(R.id.phone_number_edit_text_update_manager);
        editTextOfEmail = (EditText)findViewById(R.id.email_edit_text_add_manager);
        editTextOfYearsInCompany = (EditText)findViewById(R.id.years_in_company_edit_text_update_manager);
        male = (CheckBox) findViewById(R.id.male_checkBox_update_manager);
        female = (CheckBox) findViewById(R.id.female_checkBox_update_manager);
        updateButton = (Button)findViewById(R.id.update_manager_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_manager);
        //this user
        Intent preIntent = getIntent();
        user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);
        //function to find views by id
        findView();
        //enter the data of the manager
        enterDetail();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    id = Long.valueOf(textViewOfID.getText().toString());
                    name = editTextOfName.getText().toString();
                    address = editTextOfAddress.getText().toString();
                    phoneNumber = editTextOfPhoneNumber.getText().toString();
                    if (phoneNumber.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: Phone number must contain only digits");
                    email = editTextOfEmail.getText().toString();
                    if (email.contains("@") == false) {
                        throw new Exception("ERROR: your email address is wrong");
                    }
                    yearsInCompany = Integer.valueOf(editTextOfYearsInCompany.getText().toString());
                    if (yearsInCompany < 0) {
                        throw new Exception("ERROR: years in company most be a positive value");
                    }
                    if (male.isChecked()) {
                        gender = Gender.MALE;
                    } else if (female.isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    //try to add the manager
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Manager managerUpdated = new Manager(id, name, address, phoneNumber, email, gender,yearsInCompany);
                    backendFactory.updateManger(managerUpdated,user.getPrivilege());
                    Toast.makeText(UpdateManager.this, "The update has been successful!", Toast.LENGTH_LONG).show();

                    //go back to the activity
                    Intent intent = new Intent(UpdateManager.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, managerUpdated);//add the specific manager
                    startActivity(intent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(UpdateManager.this, "Failed to add the manager:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //function to enter the values of the selected customer by its ID
    void enterDetail()
    {
        try {
            Manager manager = BackendFactory.getInstance().getManger();
            textViewOfID.setText(String.valueOf(manager.getNumID()));
            editTextOfName.setText(manager.getName());
            editTextOfAddress.setText(manager.getAddress());
            editTextOfPhoneNumber.setText(manager.getPhoneNumber());
            editTextOfEmail.setText(manager.getEmailAddress());
            editTextOfYearsInCompany.setText(manager.getYearsInCompany());
            switch (manager.getGender())
            {
                case MALE:
                    male.setChecked(true);
                    break;
                case FEMALE:
                    female.setChecked(true);
                    break;
            }

        }
        catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(UpdateManager.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_update_manager:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_update_manager:
                male.setChecked(false);
        }
    }
}
