package com.binarybeasts.deliveryapp.Utility;

import com.google.firebase.database.FirebaseDatabase;

public class SetPersistence {
    public SetPersistence(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
