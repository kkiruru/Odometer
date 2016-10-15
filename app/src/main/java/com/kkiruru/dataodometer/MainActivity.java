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

        mOdometer = (Odometer) findViewById(R.id.odometer);
        mValue = (EditText) findViewById(R.id.value);
    }

    public void onInitialize(View view) {
        mOdometer.setNumber(1234);
    }

    public void onIncrease(View view) {
        TextView increase = (TextView) view;
        int value = Integer.parseInt(increase.getText().toString());
        mOdometer.add(value);
    }

    public void onDecrease(View view) {
        TextView decrease = (TextView) view;
        int value = Integer.parseInt(decrease.getText().toString());
        mOdometer.subtract(value);
    }


    public void onChangeValue(View view) {
        Editable editable = mValue.getEditableText();
        int value = 0;
        try {
            value = Integer.parseInt(editable.toString());
        } catch (NumberFormatException e) {

        }
        mOdometer.setNumber(value);
    }
}
