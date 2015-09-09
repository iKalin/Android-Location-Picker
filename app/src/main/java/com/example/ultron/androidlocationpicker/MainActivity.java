package com.example.ultron.androidlocationpicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mLocationButton;
    private TextView mLocationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationPickerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        mLocationButton = (Button) findViewById(R.id.location_button);
        mLocationName = (TextView) findViewById(R.id.location_name);
    }
}
