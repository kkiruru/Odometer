package com.kkiruru.dataodometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Odometer mOdometer;
    private EditText mValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOdometer = (Odometer)findViewById(R.id.odometer);
        mValue = (EditText)findViewById(R.id.value);
    }

    public void onInitialize(View view){
        mOdometer.setNumber(0);
    }

    public void onIncrease(View view) {
        int adjustValue = 0;
        TextView increase = (TextView)view;
        adjustValue = Integer.parseInt(increase.getText().toString());
        mOdometer.adjust(adjustValue);
    }

    public void onDecrease(View view) {
        int adjustValue = 0;
        TextView decrease = (TextView)view;
        adjustValue = Integer.parseInt(decrease.getText().toString());
        mOdometer.adjust(-adjustValue);
    }

    public void onIncreaseValue(View view) {
        Editable editable = mValue.getEditableText();
        int value = 0;
        try {
            value = Integer.parseInt(editable.toString());
        }catch ( NumberFormatException e ){

        }
        mOdometer.adjust(value);
    }

    public void onDecreaseValue(View view) {
        Editable editable = mValue.getEditableText();
        int value = 0;
        try {
            value = Integer.parseInt(editable.toString());
        }catch ( NumberFormatException e ){

        }
        mOdometer.adjust(-value);
    }

}
