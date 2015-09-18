package com.example.ultron.androidlocationpicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bibolbolat.locationpicker.LocationModel;
import com.bibolbolat.locationpicker.LocationPickerActivity;

public class MainActivity extends AppCompatActivity {
    private static int sLocationPickerRequestCode = 42;

    private LocationModel currentLocation;
    private TextView mLocationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button locationButton = (Button) findViewById(R.id.location_button);
        mLocationName = (TextView) findViewById(R.id.location_name);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationPickerActivity.class);
                if (currentLocation != null) {
                    intent.putExtra(LocationPickerActivity.EXTRA_LOCATION, currentLocation);
                }
                startActivityForResult(intent, sLocationPickerRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == sLocationPickerRequestCode) {
            if (resultCode == RESULT_OK) {
                currentLocation = (LocationModel) data.getSerializableExtra(LocationPickerActivity.EXTRA_LOCATION);
                mLocationName.setText(currentLocation.getName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
