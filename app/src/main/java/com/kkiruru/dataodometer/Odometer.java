package com.kkiruru.dataodometer;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    private PositionalNumber mCurrentNumber = new PositionalNumber();
    private PositionalNumber mTargetNumber = new PositionalNumber();
//    private List<NumberRing> mNumberRings = new ArrayList<>();

    private NumberRing[] mNumberRings = new NumberRing[6];


    private TextView mDot;

    private static final int NUMBER_RING_BIG_SIZE_DP = 130;
    private static final int NUMBER_RING_SMALL_SIZE_DP = 90;

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private UnitMode mUnitMode = UnitMode.MB;

    enum UnitMode {
        MB,
        GB,
        TB
    }

    private OnOdometerInteractionListener mInteractionListener;

    public interface OnOdometerInteractionListener {
        void OnChangedUnit(UnitMode unitMode);
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

        mDot = new TextView(context);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeue-Medium.otf");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);

        mDot.setLayoutParams(layoutParams);
        mDot.setTypeface(typeface);
        mDot.setTextSize(TypedValue.COMPLEX_UNIT_DIP, NUMBER_RING_SMALL_SIZE_DP);
        mDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorControlHighlight));
        mDot.setText(".");
        mDot.setGravity(Gravity.BOTTOM);
        mDot.setTranslationY(LayoutUtils.dp2Px(25));

        for (int i = 0; i < 6; i++) {
            NumberRing numberRing = createMeterNumber(i, 0);
            numberRing.setOdometerInteractionListener(NumberRingInteraction);
            mNumberRings[i] = numberRing;
        }

        resetView();
        setNumber(0);
    }

    private void resetView() {
        mTargetNumber.clear();
        removeAllViews();

        for (int i = 0; i < mNumberRings.length - 1; i++) {
            addView(mNumberRings[i], 0);
            mNumberRings[i].setVisibility(View.GONE);
        }

        addView(mDot, 3);
        mDot.setVisibility(View.GONE);

        mNumberRings[0].setVisibility(View.VISIBLE);
    }

    private void unitMode(long value) {
        if (1000000 <= value) {
            mUnitMode = UnitMode.TB;
            mDot.setVisibility(View.VISIBLE);
            NumberRing numberRing = mNumberRings[0];
            numberRing.setVisibility(View.GONE);
        } else if (1000 <= value) {
            mUnitMode = UnitMode.GB;
            mDot.setVisibility(View.VISIBLE);
            NumberRing numberRing = mNumberRings[0];
            numberRing.setVisibility(View.GONE);
        } else {
            mUnitMode = UnitMode.MB;
            mDot.setVisibility(View.GONE);
            NumberRing numberRing = mNumberRings[0];
            numberRing.setVisibility(View.VISIBLE);
        }

        if (mInteractionListener != null) {
            mInteractionListener.OnChangedUnit(mUnitMode);
        }
    }


    public void setNumber(long value) {
        Log.d("Odometer", "setNumber : " + value);
        mCurrentNumber.setValue(value);
        mTargetNumber.setValue(value);

        int i;
        for (i = 0; i < mTargetNumber.size(); i++) {
            NumberRing numberRing = mNumberRings[i];
            numberRing.setVisibility(View.VISIBLE);
            numberRing.setNumber(mTargetNumber.getPositionValue(i));
        }

        for (; i < mNumberRings.length; i++) {
            NumberRing numberRing = mNumberRings[i];
            numberRing.setVisibility(View.GONE);
        }

        unitMode(mCurrentNumber.getValue());
    }

    private NumberRing createMeterNumber(int position, long value) {
        NumberRing numberRing = new NumberRing(mContext);
        numberRing.setTextColor(ContextCompat.getColor(mContext, R.color.colorControlHighlight));
        numberRing.setTextSize(NUMBER_RING_BIG_SIZE_DP);
        numberRing.setInterpolator(mInterpolator);
        numberRing.setPositionalNumber(position);
        numberRing.setNumber(value);
        return numberRing;
    }

    //value 설정한다
    public void setNumberTo(long value) {
        Log.d("Odometer", "setNumberTo : " + value);

        setNumber(mTargetNumber.getValue());

        if (mTargetNumber.getValue() < value) {

            Log.d("Odometer", "__  increase : " + ( value - mTargetNumber.getValue()));

            mNumberRings[0].increase(value - mTargetNumber.getValue());
            mTargetNumber.setValue(value);
        } else {

            Log.d("Odometer", "__  decrease : " + ( mTargetNumber.getValue() - value));

            mNumberRings[0].decrease(mTargetNumber.getValue() - value);

            mTargetNumber.setValue(value);
        }
        Log.d("Odometer", "__  mTargetNumber : " +  mTargetNumber.getValue() );
    }

    public void add(long value) {
        Log.d("Odometer", "add : " + value);
        setNumberTo(mTargetNumber.getValue() + value);
    }

    public void subtract(long value) {
        Log.d("Odometer", "subtract " + value);
        if ( mTargetNumber.getValue() < value){
            setNumberTo(0);
        }else{
            setNumberTo(mTargetNumber.getValue() - value);
        }
    }


    private NumberRing.NumberRingInteraction NumberRingInteraction = new NumberRing.NumberRingInteraction() {
        @Override
        public void onCarry(int position, long carry) {
            Log.d("Odometer", "onCarry[" + position + "] , " + carry);
            //현재 최상위 자리에서 carry가 발생했다면, 새로운 NumberRing을 추가한다

            if (mNumberRings.length == position + 1) {
                //GB를 벗어났다
                Log.e("Odometer", "GB를 벗어났다");
            }

            NumberRing numberRing = mNumberRings[position + 1];
            if (numberRing.getVisibility() == View.GONE) {
                numberRing.setVisibility(View.VISIBLE);
                if (position + 1 == 3) {
                    //단위 변경이 되어야 한다
                    Log.e("Odometer", "단위 변경이 되어야 한다");
                } else if (position + 1 == 5) {
                    //
                    Log.e("Odometer", "100GB영역이다");
                }
                numberRing.setNumber(0);
                numberRing.increaseFromCurrentTo(carry);
            } else {
                numberRing.increase(carry);
            }

        }

        @Override
        public void onRoundDown(int position, long borrow) {
            Log.d("Odometer", "onRoundDown[" + position + "] , " + borrow);
            if (position < mNumberRings.length - 1) {
                mNumberRings[position + 1].decrease(borrow);
            }
        }

        @Override
        public void onComplete(int position, long value) {
            Log.d("Odometer", "onComplete[" + position + "] , " + value);
            mCurrentNumber.setPositionValue(position, value);
//            removeZero(mNumberRings, mNumberRings.size() - 1);
            Log.d("Odometer", "_ mCurrentNumber : " + mCurrentNumber.getValue());
        }
    };


    private void removeZero(List<NumberRing> numbers, int index) {
        if (0 < index) {
            NumberRing numberRing = numbers.get(index);
            if (numberRing != null) {
                if (numberRing.getCurrentValue() == numberRing.getTargetValue() && numberRing.getCurrentValue() == 0
                        || numberRing.getVisibility() == View.GONE) {
                    numberRing.setVisibility(View.GONE);
                    if (index <= 3) {
                        unitMode(999);
                    }
                    removeZero(numbers, index - 1);
                }
            }
        }
    }

    public void setOdometerInteractionListener(Odometer.OnOdometerInteractionListener listener) {
        mInteractionListener = listener;
    }

    public void setTextSize(int size) {
        for (NumberRing numberRing : mNumberRings) {
            numberRing.setTextSize(size);
        }

        mDot.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public int getTextSize() {
        return ((NumberRing) mNumberRings[0]).getTextSize();
    }

}

