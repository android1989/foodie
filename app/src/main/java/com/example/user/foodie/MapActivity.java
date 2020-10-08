package com.example.user.foodie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    String Latitude, Longitude, Title;
    private LatLng TutorialsPoint;
    private TextView title;
    private ImageButton back;
    private String restaurent_id;
    private int restaurent_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        title = findViewById(R.id.location_title);
        back = findViewById(R.id.back_btn);
        Intent intent = getIntent();
        Latitude = intent.getStringExtra("latitude");
        Longitude = intent.getStringExtra("longitude");
        Title  = intent.getStringExtra("title");
        restaurent_id =  intent.getStringExtra("user_id");
        restaurent_position =  intent.getIntExtra("position",0);
        TutorialsPoint = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));

        title.setText(Title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MapActivity.this,"back button clicked",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(MapActivity.this,RestaurentsActivity.class);
                intent1.putExtra("user_id",restaurent_id);
                intent1.putExtra("position",restaurent_position);
                startActivity(intent1);
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        try
        {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 15.0f));
            googleMap.addMarker(new MarkerOptions().position(TutorialsPoint)
                    .title(Title));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
