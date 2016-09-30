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

    private int mCurrentValue = 0;
    private int mTargetValue = 0;

    private PositionalNumber mCurrentNumber = new PositionalNumber();
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

        mTargetValue = mCurrentValue = val;

        if (mCurrentValue < 1000) {
            mUnitMode = UnitMode.MB;
        } else {
            mUnitMode = UnitMode.GB;
        }

        setPositionalNumber(mUnitMode, mCurrentValue, mTargetValue);

        for (int i = 0; i < mTargetNumber.size(); i++) {
            MeterNumber meterNumber = createMeterNumber(i, mTargetNumber.getPositionValue(i));
            meterNumber.setOdomenterInteractionListener(OdomenterInteraction);

            mMeterNumbers.add(meterNumber);
        }

        for (MeterNumber meterNumber : mMeterNumbers) {
            addView(meterNumber, 0);
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


    public void setNumberTo(int value) {
        mTargetValue = value;

        if (mCurrentValue < 1000) {
            mUnitMode = UnitMode.MB;
        } else {
            mUnitMode = UnitMode.GB;
        }

        setPositionalNumber(mUnitMode, mCurrentValue, mTargetValue);

        for (int i = 0; i < mTargetNumber.size(); i++) {
            MeterNumber meterNumber = createMeterNumber(i, mTargetNumber.getPositionValue(i));
            meterNumber.setOdomenterInteractionListener(OdomenterInteraction);

            mMeterNumbers.add(meterNumber);
        }

        for (MeterNumber meterNumber : mMeterNumbers) {
            addView(meterNumber, 0);
        }
    }

    public void addValue(int value) {
        Log.d("addNumber", "curr : " + mCurrentValue + ", target : " + mTargetValue + " -> " + (mTargetValue + value));
        if (value < 0) {
            return;
        }

        mTargetValue += value;

        setNumberTo(mTargetValue);
    }


    public void subtractValue(int value) {
        Log.d("subtractNumber", "curr : " + mCurrentValue + ", target : " + mTargetValue + " -> " + (mTargetValue + value));

        if (value < 0) {
            return;
        }

        mTargetValue -= value;
        if ( mTargetValue < 0 ){
            mTargetValue = 0;
        }
        setNumberTo(mTargetValue);
    }


    private MeterNumber.OdomenterInteraction OdomenterInteraction = new MeterNumber.OdomenterInteraction() {
        @Override
        public void onCarry(int position, int carry) {
            Log.e("Odometer", "onCarry [" + position + "]_" + carry);
            //현재 최상위 자리에서 carry가 발생했다면, 새로운 MeterNumber를 추가한다
            if (mMeterNumbers.size() <= position + 1) {

                if (mUnitMode == UnitMode.MB && mMeterNumbers.size() == 3) {
                    mUnitMode = UnitMode.GB;
                    Log.e("Odometer", " >> UnitMode.GB ");
                    removeViewAt(2);
                } else {

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
            mCurrentNumber.setPositionValue(position, value);
            Log.e("Odometer", "__  mCurrentNumber : " + mCurrentNumber.getValue() + ", mTargetNumber : " + mTargetNumber.getValue());

            if (mCurrentNumber.getValue() == mTargetNumber.getValue()) {
                Log.e("Odometer", "__ all completed ~~~ ");
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

