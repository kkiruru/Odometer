package com.kkiruru.dataodometer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1100416 on 16. 9. 12..
 */
public class Odometer extends LinearLayout {
    private Context mContext;

    private int mCurrent = 0;
    private int mTarget = 0;

    private PositionalNumber mCurrentValue = new PositionalNumber();
    private PositionalNumber mTargetNumber = new PositionalNumber();
    private List<MeterNumber> mMeterNumbers = new ArrayList<>();

    private int mTextSize = 90;
    private int[] mTextColors = new int[]{R.color.accent};
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

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

        setNumber(0);
    }

    private UnitMode mUnitMode = UnitMode.MB;

    enum UnitMode {
        MB,
        GB
    }

    private MeterNumber createMeterNumber(int position, int value) {
        MeterNumber meterNumber = new MeterNumber(mContext);
        meterNumber.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
        meterNumber.setTextSize(mTextSize);
        meterNumber.setInterpolator(mInterpolator);
        meterNumber.setPositionalNumber(position);
        meterNumber.setNumber(value);
        return meterNumber;
    }


    public void setNumber(int val) {
        resetView();

        mTarget = mCurrent = val;

        if ( mCurrent < 1000) {
            mUnitMode = UnitMode.MB;
        }else{
            mUnitMode = UnitMode.GB;
        }

        mTargetNumber.setValue(val);

        for (int i = 0; i < mTargetNumber.size(); i++) {
            MeterNumber meterNumber = createMeterNumber(i, mTargetNumber.getPositionValue(i));
            meterNumber.setOdomenterInteractionListener(OdomenterInteraction);

            mMeterNumbers.add(meterNumber);
        }

        for (MeterNumber meterNumber : mMeterNumbers) {
            addView(meterNumber, 0);
        }
    }


    private MeterNumber.OdomenterInteraction OdomenterInteraction = new MeterNumber.OdomenterInteraction() {
        @Override
        public void onCarry(int position, int carry) {
            Log.e("Odometer", "onCarry [" + position + "]_" + carry);
            //현재 최상위 자리에서 carry가 발생했다면, 새로운 MeterNumber를 추가한다
            if (mMeterNumbers.size() <= position + 1) {

                if ( mUnitMode == UnitMode.MB && mMeterNumbers.size() == 3 ){
                    mUnitMode = UnitMode.GB;
                    Log.e("Odometer", " >> UnitMode.GB ");
                    removeViewAt(0);
                }else{

                }

                MeterNumber meterNumber = createMeterNumber(position + 1, 0);
                meterNumber.setOdomenterInteractionListener(OdomenterInteraction);
                mMeterNumbers.add(meterNumber);
                addView(meterNumber, 0);
                meterNumber.increase(carry);
            } else { //자리 올림을 한다
                mMeterNumbers.get(position + 1).increase(carry);
            }


        }

        @Override
        public void onRoundDown(int position, int borrow) {
            Log.e("Odometer", "onRoundDown [" + position + "]_" + borrow);
            if (position < mMeterNumbers.size() - 1) {
                mMeterNumbers.get(position + 1).decrease(borrow);
            }
        }

        @Override
        public void onComplete(int position, int value) {
            Log.e("Odometer", "onComplete : [" + position + "]" + "_" + value);
            mCurrentValue.setPositionValue(position, value);
            if (mCurrentValue.getValue() == mTargetNumber.getValue()) {
                Log.e("Odometer", "all completed ~~~ " + mCurrentValue.getValue());
            }
            removeZero(mMeterNumbers);
        }
    };


    private void removeZero(List<MeterNumber> numbers) {
        if (numbers.size() <= 1) {
            return;
        }

        MeterNumber meterNumber = numbers.get(numbers.size() - 1);
        if (meterNumber.getCurrentValue() == meterNumber.getTargetValue() && meterNumber.getCurrentValue() == 0) {
            removeView(meterNumber);
            numbers.remove(meterNumber);
            removeZero(numbers);
        }
    }


    private void resetView() {
        mTargetNumber.clear();
        mMeterNumbers.clear();
        removeAllViews();
    }



    public void add(int value){
        Log.d("add", "curr : " + mCurrent + ", target : " + mTarget + " -> " + (mTarget + value));
        if ( value < 0 ){
            return;
        }

        mTarget += value;

        mTargetNumber.setValue(mTarget);
    }


    public void adjust(int value) {
        Log.d("adjust", mTargetNumber.getValue() + " -> " + (mTargetNumber.getValue() + value));

        PositionalNumber deltaValue;

        if (0 < value) {
            mTargetNumber.setValue(mTargetNumber.getValue() + value);
            deltaValue = new PositionalNumber(value);
            int increment = 0;
            for (int i = deltaValue.size() - 1; 0 <= i; i--) {
                increment = increment * 10 + deltaValue.getPositionValue(i);
                if (i <= mMeterNumbers.size() - 1) {
                    mMeterNumbers.get(i).increase(increment);
                    increment = 0;
                }
            }
        } else {
            if (mTargetNumber.getValue() < Math.abs(value)) {
                value = -mTargetNumber.getValue();
            }
            mTargetNumber.setValue(mTargetNumber.getValue() + value);
            deltaValue = new PositionalNumber(Math.abs(value));

            int increment = 0;

            for (int i = deltaValue.size() - 1; 0 <= i; i--) {
                increment = increment * 10 + deltaValue.getPositionValue(i);
                if (i <= mMeterNumbers.size() - 1) {
                    mMeterNumbers.get(i).decrease(increment);
                    increment = 0;
                }
            }

        }
    }

}

