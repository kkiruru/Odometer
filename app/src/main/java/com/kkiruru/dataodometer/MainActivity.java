package com.kkiruru.dataodometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Odometer mOdometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOdometer = (Odometer)findViewById(R.id.odometer);

//        mOdometer.setNumber(365);
    }


    public void onIncrese(View view) {
        int id = view.getId();
        int adjustValue = 0;

        switch (id) {
            case R.id.incease_1:
                adjustValue = 1;
                break;
            case R.id.incease_7:
                adjustValue = 7;
                break;
            case R.id.incease_13:
                adjustValue = 13;
                break;
            case R.id.incease_67:
                adjustValue = 67;
                break;
        }

        mOdometer.adjust(adjustValue);
    }


    public void onDecrese(View view) {
        int id = view.getId();
        int adjustValue = 0;
        switch (id) {
            case R.id.decease_1:
                adjustValue = 1;
                break;
            case R.id.decease_7:
                adjustValue = 7;
                break;
            case R.id.decease_13:
                adjustValue = 13;
                break;
            case R.id.decease_67:
                adjustValue = 67;
                break;
        }

        mOdometer.adjust(-adjustValue);
    }


    public void onInitialize(View view){
        mOdometer.setNumber(0);
    }
}
