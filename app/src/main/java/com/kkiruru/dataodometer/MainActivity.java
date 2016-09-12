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

        mOdometer.setNumber(0);
    }


    public void onIncrese(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.incease_1:
                break;
            case R.id.incease_7:
                break;
            case R.id.incease_13:
                break;
            case R.id.incease_137:
                break;
        }


        mOdometer.setNumber(0, 9);
    }


    public void onDecrese(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.decease_1:
                break;
            case R.id.decease_7:
                break;
            case R.id.decease_13:
                break;
            case R.id.decease_137:
                break;
        }
    }

}
