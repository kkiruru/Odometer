package com.kkiruru.dataodometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Odometer.OnOdometerInteractionListener {
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
        mOdometer.setNumber(0);
    }

    public void onIncrease(View view) {
        TextView increase = (TextView) view;
        long value = Long.parseLong(increase.getText().toString());
        mOdometer.add(value);
    }

    public void onDecrease(View view) {
        TextView decrease = (TextView) view;
        long value = Long.parseLong(decrease.getText().toString());
        mOdometer.subtract(value);
    }

    public void onSettingTo(View view) {
        Editable editable = mValue.getEditableText();
        long value = 0;
        try {
            value = Long.parseLong(editable.toString());
        } catch (NumberFormatException e) {

        }
        mOdometer.setNumber(value);
    }

    public void onChangeValue(View view) {
        Editable editable = mValue.getEditableText();
        long value = 0;
        try {
            value = Long.parseLong(editable.toString());
        } catch (NumberFormatException e) {

        }
        mOdometer.setNumberTo(value);
    }

    public void onAdd(View view) {
        Editable editable = mValue.getEditableText();
        long value = 0;
        try {
            value = Long.parseLong(editable.toString());
        } catch (NumberFormatException e) {

        }
        mOdometer.add(value);
    }

    public void onSubtract(View view) {
        Editable editable = mValue.getEditableText();
        long value = 0;
        try {
            value = Long.parseLong(editable.toString());
        } catch (NumberFormatException e) {

        }
        mOdometer.subtract(value);
    }


    @Override
    public void OnChangedUnit(Odometer.UnitMode unitMode) {

    }
}
