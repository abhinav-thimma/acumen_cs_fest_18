package com.example.saiprasadgarimella.navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        /*final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1195306386849471/5091452545");

        AdRequest request = new AdRequest.Builder().build();

        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);

        final SharedPreferences eventData = getSharedPreferences("eventData", MODE_PRIVATE);
        int eid = eventData.getInt("EID", 0);

        switch (eid) {
            case 1:
                title.setText(getString(R.string.event1));

                break;
            case 2:
                title.setText(getString(R.string.event2));

                break;
            case 3:
                title.setText(getString(R.string.event3));

                break;
            case 4:
                title.setText(getString(R.string.event4));

                break;
            case 5:
                title.setText(getString(R.string.event5));

                break;
            case 6:
                title.setText(getString(R.string.event6));

                break;
            case 7:
                title.setText(getString(R.string.event7));

                break;
            case 8:
                title.setText(getString(R.string.event8));

                break;
            case 9:
                title.setText(getString(R.string.event9));

                break;
            case 10:
                title.setText(getString(R.string.event10));

                break;
            case 11:
                title.setText(getString(R.string.event11));

                break;
            case 12:
                title.setText(getString(R.string.event12));

                break;
            case 13:
                title.setText(getString(R.string.event13));

                break;
            case 14:
                title.setText(getString(R.string.event14));

                break;
            case 15:
                title.setText(getString(R.string.event15));

                break;
            case 16:
                title.setText(getString(R.string.event16));

                break;
            case 17:
                title.setText(getString(R.string.event17));

                break;
            case 18:
                title.setText(getString(R.string.event18));

                break;
            case 19:
                title.setText(getString(R.string.event19));

                break;
        }
*/
        Button done = (Button) findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterSuccessActivity.this, MainActivity.class));
                finish();
            }
        });
    }
    }

