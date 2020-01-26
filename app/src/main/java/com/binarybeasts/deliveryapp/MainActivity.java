package com.binarybeasts.deliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

    private LocationManager locationManager;
    private String provider;
    Double lat,lng;

    DeliveryRequestAdapter adapter;

    LocationManager lm;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("ak47", "getLocation: "+provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            //onLocationChanged(location);
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d("ak47", "getLocation: "+lat+" "+lng);
            reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("address").setValue(lat+" "+lng);
        } else{
            locationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("address").setValue(location.getLatitude()+" "+location.getLongitude());
                    Log.d("ak47", "onLocationChanged: ");

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getLocation();
                }

                break;
            default:
                Log.e("ak47", "onRequestPermissionsResult: "+requestCode );
        }
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
                    deliveryRequests.setUID(dataSnapshot1.getKey());
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
        Toast.makeText(this,"Accepted"+list.get(position).getUID(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void decline(int position) {
        Toast.makeText(this,"Declined"+list.get(position).getUID(),Toast.LENGTH_LONG).show();

    }
}
