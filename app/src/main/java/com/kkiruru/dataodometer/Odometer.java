package com.kkiruru.dataodometer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1100416 on 16. 9. 12..
 */
public class Odometer extends LinearLayout {
    private Context mContext;

    private int mCurrentValue = 0;
    private int mTargetValue = 0;

    private PositionalNumber mCurrentNumber = new PositionalNumber();
    private PositionalNumber mTargetNumber = new PositionalNumber();
    private List<NumberRing> mNumberRings = new ArrayList<>();

    private TextView mUnit;
    private TextView mDot;

    private int mTextSize = 90;
    private int[] mTextColors = new int[]{R.color.accent};
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private UnitMode mUnitMode = UnitMode.MB;

    enum UnitMode {
        MB,
        GB
    }

    public Odometer(Context context) {
        this(context, null);
    }

    public Odometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Odometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        mUnit = new TextView(context);
        mDot = new TextView(context);
        init();
    }

    private void init() {
        mUnit.setText("MB");
        mDot.setText(".");
        resetView();
        setNumber(0);
    }


    private void resetView() {
        mTargetNumber.clear();
        mNumberRings.clear();
        removeAllViews();

        addView(mUnit);

        NumberRing numberRing = createMeterNumber(0, 0);
        numberRing.setOdomenterInteractionListener(OdomenterInteraction);
        mNumberRings.add(numberRing);
        addView(numberRing, 0);

        numberRing = createMeterNumber(1, 1);
        numberRing.setOdomenterInteractionListener(OdomenterInteraction);
        mNumberRings.add(numberRing);
        numberRing.setVisibility(View.GONE);
        addView(numberRing, 0);

        mDot.setVisibility(View.GONE);
        addView(mDot, 0);

        numberRing = createMeterNumber(2, 2);
        numberRing.setOdomenterInteractionListener(OdomenterInteraction);
        mNumberRings.add(numberRing);
        numberRing.setVisibility(View.GONE);
        addView(numberRing, 0);
    }


    private void unitMode(){
        NumberRing numberRing = mNumberRings.get(1);
        numberRing.setVisibility(View.VISIBLE);
        numberRing = mNumberRings.get(2);
        numberRing.setVisibility(View.VISIBLE);
        mUnit.setText("GB");
        mDot.setVisibility(View.VISIBLE);
    }


    public void setNumber(int value) {
        mCurrentValue = mTargetValue = value;

        if (mCurrentValue < 1000) {
            mUnitMode = UnitMode.MB;
            mUnit.setText("MB");
            mDot.setVisibility(View.GONE);
        } else {
            mUnitMode = UnitMode.GB;
            mUnit.setText("GB");
            mDot.setVisibility(View.VISIBLE);
        }

        setPositionalNumber(mUnitMode, mCurrentValue, mTargetValue);

        for (int i = 0; i < mTargetNumber.size(); i++) {
            NumberRing numberRing;
            if ( i < mNumberRings.size() ){
                numberRing = mNumberRings.get(i);
                numberRing.setNumber(mTargetNumber.getPositionValue(i));
                numberRing.setVisibility(View.VISIBLE);
            }else{
                numberRing = createMeterNumber(i, mTargetNumber.getPositionValue(i));
                numberRing.setOdomenterInteractionListener(OdomenterInteraction);
                numberRing.setNumber(mTargetNumber.getPositionValue(i));
                mNumberRings.add(numberRing);
                addView(numberRing, 0);
            }
        }

        if ( mTargetNumber.size() < mNumberRings.size() ){
            for ( int i = mTargetNumber.size() ; i < mNumberRings.size(); i++ ){
                NumberRing numberRing = mNumberRings.get(i);
                numberRing.setVisibility(View.GONE);
            }
        }
    }

    private NumberRing createMeterNumber(int position, int value) {
        NumberRing numberRing = new NumberRing(mContext);
        numberRing.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
        numberRing.setTextSize(mTextSize);
        numberRing.setInterpolator(mInterpolator);
        numberRing.setPositionalNumber(position);
        numberRing.setNumber(value);
        return numberRing;
    }

    public void setNumberTo(int value) {
        mTargetValue = value;

        if (mCurrentValue < 1000) {
            mUnitMode = UnitMode.MB;
        } else {
            mUnitMode = UnitMode.GB;
        }

        setPositionalNumber(mUnitMode, mCurrentValue, mTargetValue);

        for (int i = 0; i < mTargetNumber.size(); i++) {
            NumberRing numberRing = createMeterNumber(i, mTargetNumber.getPositionValue(i));
            numberRing.setOdomenterInteractionListener(OdomenterInteraction);

            mNumberRings.add(numberRing);
        }

        for (NumberRing numberRing : mNumberRings) {
            addView(numberRing, 0);
        }
    }


    public void add(int value) {
        PositionalNumber deltaValue;
        mTargetNumber.setValue(mTargetNumber.getValue() + value);
        deltaValue = new PositionalNumber(value);
        int increment = 0;
        for (int i = deltaValue.size() - 1; 0 <= i; i--) {
            increment = increment * 10 + deltaValue.getPositionValue(i);
            if (i <= mNumberRings.size() - 1) {
                mNumberRings.get(i).increase(increment);
                increment = 0;
            }
        }
    }


    public void subtract(int value) {
        PositionalNumber deltaValue;
        if (mTargetNumber.getValue() < Math.abs(value)) {
            value = -mTargetNumber.getValue();
        }
        mTargetNumber.setValue(mTargetNumber.getValue() + value);
        deltaValue = new PositionalNumber(Math.abs(value));

        int increment = 0;

        for (int i = deltaValue.size() - 1; 0 <= i; i--) {
            increment = increment * 10 + deltaValue.getPositionValue(i);
            if (i <= mNumberRings.size() - 1) {
                mNumberRings.get(i).decrease(increment);
                increment = 0;
            }
        }
    }


    private void setPositionalNumber(UnitMode mode, int currentValue, int targetValue) {
        if (mode == UnitMode.MB) {
            mCurrentNumber.setValue(currentValue);
            mTargetNumber.setValue(targetValue);
        } else {
            mCurrentNumber.setValue(currentValue / 10);
            mTargetNumber.setValue(targetValue / 10);
        }
    }


    private NumberRing.OdomenterInteraction OdomenterInteraction = new NumberRing.OdomenterInteraction() {
        @Override
        public void onCarry(int position, int carry) {
            Log.e("Odometer", "onCarry [" + position + "]_" + carry);
            //현재 최상위 자리에서 carry가 발생했다면, 새로운 MeterNumber를 추가한다
            if (mNumberRings.size() <= position + 1) {
                if (mUnitMode == UnitMode.MB && mNumberRings.size() == 3) {
                    mUnitMode = UnitMode.GB;
                    mUnit.setText("GB");
                    Log.e("Odometer", " >> UnitMode.GB ");
                    removeViewAt(2);
                } else {

                }

                NumberRing numberRing = createMeterNumber(position + 1, 0);
                numberRing.setOdomenterInteractionListener(OdomenterInteraction);
                mNumberRings.add(numberRing);
                addView(numberRing, 0);
                numberRing.increase(carry);
            } else { //자리 올림을 한다
                mNumberRings.get(position + 1).increase(carry);
            }
        }

        @Override
        public void onRoundDown(int position, int borrow) {
            Log.e("Odometer", "onRoundDown [" + position + "]_" + borrow);
            if (position < mNumberRings.size() - 1) {
                mNumberRings.get(position + 1).decrease(borrow);
            }
        }

        @Override
        public void onComplete(int position, int value) {
            Log.e("Odometer", "onComplete : [" + position + "]" + "_" + value);
            mCurrentNumber.setPositionValue(position, value);
            Log.e("Odometer", "__  mCurrentNumber : " + mCurrentNumber.getValue() + ", mTargetNumber : " + mTargetNumber.getValue());

            if (mCurrentNumber.getValue() == mTargetNumber.getValue()) {
                Log.e("Odometer", "__ all completed ~~~ ");
            }
            removeZero(mNumberRings);
        }
    };


    private void removeZero(List<NumberRing> numbers) {
        if (numbers.size() <= 1) {
            return;
        }

        NumberRing numberRing = numbers.get(numbers.size() - 1);
        if (numberRing.getCurrentValue() == numberRing.getTargetValue() && numberRing.getCurrentValue() == 0) {
            removeView(numberRing);
            numbers.remove(numberRing);
            removeZero(numbers);
        }
    }




    public void adjust(int value) {
        Log.d("adjust", mTargetNumber.getValue() + " -> " + (mTargetNumber.getValue() + value));

        if (0 < value) {
            add(value);
        } else {
            subtract(value);
        }
    }

    public int getValue() {
        return mTargetValue;
    }
}

