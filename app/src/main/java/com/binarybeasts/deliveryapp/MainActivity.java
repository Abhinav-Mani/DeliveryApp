package com.binarybeasts.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.binarybeasts.deliveryapp.Activities.LoginActivity;
import com.binarybeasts.deliveryapp.Adapter.DeliveryRequestAdapter;
import com.binarybeasts.deliveryapp.Model.DeliveryPerson;
import com.binarybeasts.deliveryapp.Model.DeliveryRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DeliveryRequestAdapter.ClickHandler {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ArrayList<DeliveryRequests> list;

    DeliveryRequestAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        checkNewUser();

        fetch();
    }

    private void fetch() {
        reference.child("Delivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    DeliveryRequests deliveryRequests = dataSnapshot1.getValue(DeliveryRequests.class);
                    list.add(deliveryRequests);
                    Log.d("ak47", "onDataChange: "+deliveryRequests.getAmount());
                    adapter.notifyItemInserted(list.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkNewUser() {
        if(getIntent().hasExtra("Delivery")){
            DeliveryPerson deliveryPerson=(DeliveryPerson) getIntent().getSerializableExtra("Delivery");
            reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(deliveryPerson);
        }
    }

    private void init() {
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        list=new ArrayList<>();

        recyclerView=findViewById(R.id.deliveryRequestList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new DeliveryRequestAdapter(list,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void accept(int position) {
        Toast.makeText(this,"Accepted",Toast.LENGTH_LONG).show();

    }

    @Override
    public void decline(int position) {
        Toast.makeText(this,"Declined",Toast.LENGTH_LONG).show();

    }
}
