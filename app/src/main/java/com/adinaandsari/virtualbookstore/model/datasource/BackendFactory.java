package com.adinaandsari.virtualbookstore.model.datasource;

import com.adinaandsari.virtualbookstore.model.backend.Backend;

/**
 * Created by adina_000 on 19-Nov-15.
 */
public class BackendFactory {
    static Backend instance = null;

    public final static Backend getInstance()
    {
        if (instance == null)
              instance = new com.adinaandsari.virtualbookstore.model.datasource.DatabaseList();
        return instance;

    }
}
