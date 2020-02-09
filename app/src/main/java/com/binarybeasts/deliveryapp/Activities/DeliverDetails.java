package com.binarybeasts.deliveryapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.binarybeasts.deliveryapp.Model.DeliveryRequests;
import com.binarybeasts.deliveryapp.R;
import com.binarybeasts.deliveryapp.Utility.Checker;
import com.binarybeasts.deliveryapp.Utility.OPTGenerator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class DeliverDetails extends AppCompatActivity implements View.OnClickListener {
    Button requestFarmerOTP,requestCustomerOTP,verifyFarmerOTP,verifyCustomerOTP,callFarmer,callCustomer,directionFarmer,directionCustomer;
    private String farmerOtp,customerOtp;
    Button BadQuality;
    DatabaseReference reference;
    OPTGenerator optGenerator;
    DeliveryRequests deliveryRequests;
    Checker checker;
    EditText farmerEnteredOTP,customerEnteredOtp;
    LinearLayout farmerSection,customerSection,badSection,completeSection;
    ImageView photo;
    int clickedImage=0;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_details);

        check();

        init();

        checkFeatures();

        setListeners();
    }

    private void checkFeatures() {
        farmerSection.setVisibility(View.GONE);
        customerSection.setVisibility(View.GONE);
        if(!checker.isMapCoordinates(deliveryRequests.getCustomerAddress())){
            directionCustomer.setEnabled(false);
        }
        if(!checker.isMapCoordinates(deliveryRequests.getFarmersAddress())){
            directionFarmer.setEnabled(false);
        }
        String[] s=deliveryRequests.getUID().split("\\|-\\|-\\|");
        reference.child("Requests").child(s[1]).child(s[0]).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status=(String) dataSnapshot.getValue();
                Toast.makeText(DeliverDetails.this,"status:-"+status,Toast.LENGTH_LONG).show();
                if(status.equalsIgnoreCase("Out For Delivery")) {
                    customerSection.setVisibility(View.VISIBLE);
                    farmerSection.setVisibility(View.VISIBLE);
                    completeSection.setVisibility(View.GONE);
                    badSection.setVisibility(View.GONE);
                }
                else if(status.equalsIgnoreCase("Received Product From Farmer")){
                    customerSection.setVisibility(View.VISIBLE);
                    farmerSection.setVisibility(View.VISIBLE);
                    completeSection.setVisibility(View.GONE);
                    badSection.setVisibility(View.GONE);
                } else if(status.equalsIgnoreCase("Bad Quality")){
                    customerSection.setVisibility(View.GONE);
                    farmerSection.setVisibility(View.GONE);
                    completeSection.setVisibility(View.GONE);
                    badSection.setVisibility(View.VISIBLE);
                } else if(status.equalsIgnoreCase("Delivered")) {
                    customerSection.setVisibility(View.GONE);
                    farmerSection.setVisibility(View.GONE);
                    completeSection.setVisibility(View.VISIBLE);
                    badSection.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setListeners() {
        requestCustomerOTP.setOnClickListener(this);
        requestFarmerOTP.setOnClickListener(this);
        verifyFarmerOTP.setOnClickListener(this);
        verifyCustomerOTP.setOnClickListener(this);
        callFarmer.setOnClickListener(this);
        callCustomer.setOnClickListener(this);
        directionCustomer.setOnClickListener(this);
        directionFarmer.setOnClickListener(this);
        BadQuality.setOnClickListener(this);
        photo.setOnClickListener(this);
    }


    private void init() {
        mStorageRef= FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(String.valueOf(System.currentTimeMillis()));
        photo=findViewById(R.id.productPhoto);
        badSection=findViewById(R.id.badSection);
        completeSection=findViewById(R.id.completeSection);
        BadQuality=findViewById(R.id.BADQuality);
        reference= FirebaseDatabase.getInstance().getReference();
        requestFarmerOTP=findViewById(R.id.requestFarmerOTP);
        requestCustomerOTP=findViewById(R.id.requestCustomerOTP);
        verifyFarmerOTP=findViewById(R.id.verifyFarmerOTP);
        verifyCustomerOTP=findViewById(R.id.verifyCustomerOTP);
        callFarmer=findViewById(R.id.callFarmer);
        callCustomer=findViewById(R.id.callCustomer);
        directionCustomer=findViewById(R.id.directionCustomer);
        directionFarmer=findViewById(R.id.directionFarmer);
        farmerEnteredOTP=findViewById(R.id.farmerOTPEditText);
        customerEnteredOtp=findViewById(R.id.customerOTPEditText);
        farmerSection=findViewById(R.id.FarmerSection);
        customerSection=findViewById(R.id.CustomerSection);
        optGenerator=new OPTGenerator();
        checker=new Checker();

    }
    private void verifyFarmerOtp()
    {
        String text=farmerEnteredOTP.getText().toString().trim();
        if(text.equals(farmerOtp)){
            String[] s = deliveryRequests.getUID().split("\\|-\\|-\\|");
            reference.child("Requests").child(s[1]).child(s[0]).child("status").setValue("Received Product From Farmer");
            //Toast.makeText(this,s[0]+"   "+s[1],Toast.LENGTH_LONG).show();
        }
    }

    private void badQuality()
    {

            String[] s = deliveryRequests.getUID().split("\\|-\\|-\\|");
            reference.child("Requests").child(s[1]).child(s[0]).child("status").setValue("Bad Quality");
            Toast.makeText(this,s[0]+"   "+s[1],Toast.LENGTH_LONG).show();
            updateFarmer();

    }

    private void updateFarmer() {
        reference.child("Farmers").child(deliveryRequests.getFarmersNumber()).child("Rejects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    reference.child("Farmers").child(deliveryRequests.getFarmersNumber()).child("Rejects").setValue(1);
                }else {
                    long val=(long)dataSnapshot.getValue()+1;
                    reference.child("Farmers").child(deliveryRequests.getFarmersNumber()).child("Rejects").setValue(val);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void check() {
        Intent intent=getIntent();
        if(intent.hasExtra("data")){
            deliveryRequests=(DeliveryRequests) intent.getSerializableExtra("data");
        }
        else {
            finish();
        }
    }
    public void RequestOTP(String phoneNo,String otp){
        reference.child("OTP").child(phoneNo).setValue(otp);
    }


    @Override
    public void onClick(View view) {
       if(view==requestFarmerOTP){
           farmerOtp=optGenerator.getOTP(5);
           RequestOTP(deliveryRequests.getFarmersNumber(),farmerOtp);
       }else if (view==requestCustomerOTP){
           customerOtp=optGenerator.getOTP(5);
           RequestOTP(deliveryRequests.getCustomerPhoneNo(),customerOtp);
       } else if(view==directionFarmer){
           getDirection(deliveryRequests.getFarmersAddress());
       } else if(view==directionCustomer){
           getDirection(deliveryRequests.getCustomerAddress());
       }else if(view==callCustomer){
           call(deliveryRequests.getCustomerPhoneNo());
       } else if(view==callFarmer){
           call(deliveryRequests.getFarmersNumber());
       } else if(view==verifyCustomerOTP){
           verifyCustomerOTP();
       } else if(view==verifyFarmerOTP){
            verifyFarmerOtp();
       } else if(view==BadQuality){
            badQuality();
       } else if(view==photo){
           clickAndUpload();
       }
    }

    private void clickAndUpload() {
        click();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&& requestCode==0) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            photo.setImageBitmap(b);
            clickedImage=1;
            upload();
        }
    }

    private void upload() {
        Bitmap mainImage=((BitmapDrawable)photo.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mainImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask mainImageUpload=mStorageRef.putBytes(data);
        mainImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String ImageUrl=String.valueOf(uri);
                        String[] s = deliveryRequests.getUID().split("\\|-\\|-\\|");
                        reference.child("Requests").child(s[1]).child(s[0]).child("img").setValue(ImageUrl);
                    }
                });
            }
        });

    }

    private void click() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,0);

        }
    }

    private void verifyCustomerOTP() {
        String text=customerEnteredOtp.getText().toString().trim();
        if(text.equals(customerOtp)){
            String[] s = deliveryRequests.getUID().split("\\|-\\|-\\|");
            reference.child("Requests").child(s[1]).child(s[0]).child("status").setValue("Delivered");
            //Toast.makeText(this,s[0]+"   "+s[1],Toast.LENGTH_LONG).show();
        }
    }


    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    private void getDirection(String Address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+Address.replace("",","));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
