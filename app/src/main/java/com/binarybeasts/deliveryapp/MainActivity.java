package com.binarybeasts.deliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.binarybeasts.deliveryapp.Activities.DeliverDetails;
import com.binarybeasts.deliveryapp.Activities.LoginActivity;
import com.binarybeasts.deliveryapp.Adapter.DeliveryRequestAdapter;
import com.binarybeasts.deliveryapp.Model.DeliveryPerson;
import com.binarybeasts.deliveryapp.Model.DeliveryRequests;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements DeliveryRequestAdapter.ClickHandler {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    HashSet<String> set;
    DeliveryPerson deliveryPerson;

    RecyclerView recyclerView;
    ArrayList<DeliveryRequests> list;


    Double lat,lng;

    DeliveryRequestAdapter adapter;

    LocationManager locationManager;
    LocationListener locationListener;

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
                else
                {
                    fetch();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setListeners();

        checkNewUser();

        //fetch();


        getLocation();

    }

    private void setListeners() {
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String pos=location.getLatitude()+" "+location.getLongitude();
                reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .child("address").setValue(pos).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ak47", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ak47", "onFailure: "+e.getMessage());
                    }
                });
                //Toast.makeText(MainActivity.this,"Location CHANGED"+pos,Toast.LENGTH_SHORT).show();
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
        };
    }

    private void getLocation() {
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        String pos=lastKnownLocation.getLatitude()+" "+lastKnownLocation.getLongitude();
        reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("address").setValue(pos);
    }


    private void fetch() {
        reference.child("DeliveryBoy").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryPerson=dataSnapshot.getValue(DeliveryPerson.class);

                adapter=new DeliveryRequestAdapter(list,MainActivity.this,deliveryPerson);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Delivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    DeliveryRequests deliveryRequests = dataSnapshot1.getValue(DeliveryRequests.class);
                    boolean isDriverElse=deliveryRequests.getDriver()==null||deliveryRequests.getDriver().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    if(!set.contains(dataSnapshot1.getKey())&&isDriverElse)
                    {
                        list.add(deliveryRequests);
                        deliveryRequests.setUID(dataSnapshot1.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkNewUser() {
        if(getIntent().hasExtra("Delivery")){
            deliveryPerson=(DeliveryPerson) getIntent().getSerializableExtra("Delivery");
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
        adapter=new DeliveryRequestAdapter(list,this,deliveryPerson);
        recyclerView.setAdapter(adapter);
        set=new HashSet<>();
    }

    @Override
    public void accept(int position) {
        DeliveryRequests delivery=list.get(position);
        String uid=delivery.getUID()+"";
        delivery.setDriver(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        delivery.setUID(null);
        reference.child("Delivery").child(uid).setValue(delivery);
        String[] s = uid.split("\\|-\\|-\\|");
        reference.child("Requests").child(s[1]).child(s[0]).child("status").setValue("Out For Delivery");

    }

    @Override
    public void decline(int position) {
        DeliveryRequests deliveryRequests=list.get(position);
        reference.child("Delivery").child(deliveryRequests.getUID()).child("driver").removeValue();
        set.add(list.get(position).getUID());
        list.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void details(int position) {
        DeliveryRequests deliveryRequests=list.get(position);
        Intent intent=new Intent(MainActivity.this, DeliverDetails.class);
        intent.putExtra("data",deliveryRequests);
        startActivity(intent);
        Toast.makeText(this,"Details",Toast.LENGTH_SHORT).show();
    }
}
