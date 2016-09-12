package com.kkiruru.dataodometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
