package com.adinaandsari.virtualbookstore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Spinner;

import com.adinaandsari.virtualbookstore.entities.Manager;

public class RemoveOpinion extends AppCompatActivity {

    Manager user;
    private Spinner removeOpinionIdSpinner;
    private int idToRemove;
    private Button removeOpinionButton,showOpinionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_opinion);


    }

}
