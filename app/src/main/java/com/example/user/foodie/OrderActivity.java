package com.example.user.foodie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.foodie.Config.Config;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private ImageView order_item_image;
    private TextView order_item_name;
    private TextView order_item_description;
    private TextView order_item_price;
    private EditText order_address;
    private ImageView gps;
    private Button pay_now_btn;
    private String item_key;
    private String item_user_id;
    private FirebaseUser current_user;
    private String current_user_id;
    private DatabaseReference item_ref;
    private String item_name;
    private String item_image;
    private String item_description;
    private String item_price = "10";

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;
    double mlongitude,mlatitude;
    String maddress;

    public static final int PAYPAL_REQUEST_CODE=7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private String postal_address;
    private String item_price_plate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        order_item_image = findViewById(R.id.order_item_image);
        order_item_name = findViewById(R.id.order_item_name);
      //  order_item_description = findViewById(R.id.order_item_description);
        order_item_price = findViewById(R.id.order_item_price);
        order_address = findViewById(R.id.order_address);
        gps = findViewById(R.id.order_location);
        pay_now_btn = findViewById(R.id.place_order_btn);

        Intent intent = getIntent();
        item_key = intent.getStringExtra("item_key");
        item_user_id = intent.getStringExtra("item_user_id");

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        item_ref = FirebaseDatabase.getInstance().getReference().child("Items").child(item_user_id).child(item_key);
        item_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
             item_name =  dataSnapshot.child("name").getValue().toString();
             item_image = dataSnapshot.child("image").getValue().toString();
             item_description = dataSnapshot.child("descriptions").getValue().toString();
             item_price_plate = dataSnapshot.child("price").getValue().toString();

                order_item_name.setText("Name:"+" "+item_name);
               // order_item_description.setText(item_description);
                order_item_price.setText("Price:"+" "+item_price_plate+" "+"Rs");

                Picasso.with(OrderActivity.this).load(item_image).fit().centerCrop()
                        .placeholder(R.drawable.restaurent_icon)
                        .error(R.drawable.restaurent_icon)
                        .into(order_item_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       //starting the service
        Intent service_intent = new Intent(OrderActivity.this,PayPalService.class);
        service_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(service_intent);

        pay_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              postal_address = order_address.getText().toString();
              processPayment();

            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OrderActivity.this);
                getLastLocation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OrderActivity.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OrderActivity.this);
    }

    //------------------AutoDetect location code--------------------------------------//


    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled())
            {
                //  Toast.makeText(AutoDetectLocationActivity.this, "location 2", Toast.LENGTH_SHORT).show();
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null)
                                {
                                    // Toast.makeText(AutoDetectLocationActivity.this, "location 3", Toast.LENGTH_SHORT).show();
                                    requestNewLocationData();
                                } else
                                {
                                    // Toast.makeText(AutoDetectLocationActivity.this, "location 4", Toast.LENGTH_SHORT).show();
                                    mlatitude = location.getLatitude();
                                    mlongitude = location.getLongitude();
                                    geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());

                                    try
                                    {
                                        //  Toast.makeText(AutoDetectLocationActivity.this, "location 5", Toast.LENGTH_SHORT).show();
                                        addresses = geocoder.getFromLocation(mlatitude, mlongitude, 1);
                                    }
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    String address = addresses.get(0).getAddressLine(0);
                                    order_address.setText(address);
                                    maddress = address;
                                    // Toast.makeText(AutoDetectLocationActivity.this, "location 6", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
            else
            {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                // Toast.makeText(this, "location 1", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            requestPermissions();
        }
    }


//------------------

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        // Toast.makeText(AutoDetectLocationActivity.this, "location 7", Toast.LENGTH_SHORT).show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }




    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            // Toast.makeText(AutoDetectLocationActivity.this, "location 8", Toast.LENGTH_SHORT).show();
            mlatitude = mLastLocation.getLatitude();
            mlongitude = mLastLocation.getLongitude();
            geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());

            try
            {
                addresses = geocoder.getFromLocation(mlatitude, mlongitude, 1);
                // Toast.makeText(AutoDetectLocationActivity.this, "location 9", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //  Toast.makeText(AutoDetectLocationActivity.this, "location 10", Toast.LENGTH_SHORT).show();
            String address = addresses.get(0).getAddressLine(0);
            order_address.setText(address);
            maddress = address;
        }
    };
//------------------------------------------------------------\\

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();

            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
          getLastLocation();
      }

    }






    //------------------AutoDetect location code ends--------------------------------------//

    @Override
    protected void onDestroy()
    {
        stopService(new Intent(OrderActivity.this,PayPalService.class));
        super.onDestroy();
    }

    private void processPayment()
    {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(item_price)),"USD","Pay for "+ item_name,
                PayPalPayment.PAYMENT_INTENT_SALE );
        Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null)
                {
                    try
                    {
                        String payment_details = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("payment_details",payment_details)
                                .putExtra("amount",item_price)
                        );

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }//inner If ends

                else
                {

                }//inner else ends

            }//outer If ends(resultCode)

            else if(resultCode == Activity.RESULT_CANCELED)
            {
           Toast.makeText(OrderActivity.this,"ordering cancelled..",Toast.LENGTH_SHORT).show();
            }//inner else ends

        }//if requestCode
        else if(requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(OrderActivity.this,"Invalid transaction..",Toast.LENGTH_SHORT).show();
        }
    }
}
