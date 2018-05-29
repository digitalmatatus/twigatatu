package com.digitalmatatus.twigatatu.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.digitalmatatus.twigatatu.R;
import com.github.paolorotolo.appintro.AppIntroFragment;


/**
 * Created by stephineosoro on 05/09/16.
 */
public class MyIntro extends BaseIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*// Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        // Instead of fragments, you can also use our default slide #66B4F2
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

        addSlide(AppIntroFragment.newInstance("Welcome", "Easily manage and track and your daily commute fare!", R.drawable.add_fare, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Ride Condition","Set up ride conditions to know how it affect your fare", R.drawable.ride_conditions, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Analytics",  "Visualize the amount you use on a weekly basis!", R.drawable.graph, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Data Collection", "Easily collect transit data using Twiga Tatu!", R.drawable.collect_data, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Enable Data Collection",  "Click settings to enable route data collection mode", R.drawable.instruction, Color.parseColor("#34495e")));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        /*// Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(false);
        showDoneButton(true);*/

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
       /* setVibrate(true);
        setVibrateIntensity(30);*/
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        /*Intent intent = new Intent(getApplicationContext(),HomeChoose.class);
        startActivity(intent);*/
//        loadMainActivity();
        finish();
        Toast.makeText(getApplicationContext(), "Skipping", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        /*Intent intent = new Intent(getApplicationContext(),HomeChoose.class);
        startActivity(intent);*/
//        loadMainActivity();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}