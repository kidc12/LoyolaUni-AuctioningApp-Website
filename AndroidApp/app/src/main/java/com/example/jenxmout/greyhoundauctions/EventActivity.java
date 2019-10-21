package com.example.jenxmout.greyhoundauctions;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_fundraiser_info);

        final ImageView eventImg = (ImageView) findViewById(R.id.fundraiserImage);
        final TextView eventDescription = (TextView) findViewById(R.id.fundraiserDescription);

        //grabs the resId for the event image and sets the ImageView to that image
        int resID = getResources().getIdentifier("inner_harbor_info_pic", "drawable",
                getPackageName());
        eventImg.setImageResource(resID);

        eventDescription.setText("This is a fundraiser to support the cleanliness of the Inner " +
                "Harbor, all proceeds go towards keeping the Inner Harbor clean!");
    }

}