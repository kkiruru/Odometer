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
    private List<Integer> mTargetNumbers = new ArrayList<>();
    private List<Integer> mPrimaryNumbers = new ArrayList<>();
    //    private List<ScrollNumber> mScrollNumbers = new ArrayList<>();
    private int mTextSize = 90;
    private int[] mTextColors = new int[]{R.color.accent};

    private ScrollNumber mScrollNumber;


    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private String mFontFileName;

    public Odometer(Context context) {
        this(context, null);
    }

    public Odometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Odometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

//        setNumber(0, 0);
//        setTextSize(mTextSize);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void setNumber(int val) {
        resetView();

        int number = val;
        do {
            int i = number % 10;
            mTargetNumbers.add(i);
            number /= 10;
        } while (number > 0);

//        for (int i = mTargetNumbers.size() - 1; i >= 0; i--) {
//            ScrollNumber scrollNumber = new ScrollNumber(mContext);
//            scrollNumber.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
//            scrollNumber.setTextSize(mTextSize);
//            scrollNumber.setInterpolator(mInterpolator);
//            if (!TextUtils.isEmpty(mFontFileName))
//                scrollNumber.setTextFont(mFontFileName);
//            scrollNumber.setNumber(0, mTargetNumbers.get(i), i * 10);
//
//            scrollNumber.setOnOdomenterInteractionListener( new ScrollNumber.OnOdomenterInteraction (){
//                @Override
//                public void onCarry() {
//
//                }
//
//                @Override
//                public void onRoundDown() {
//
//                }
//            });
//
//            mScrollNumbers.add(scrollNumber);

        if ( mScrollNumber == null ){
            mScrollNumber = new ScrollNumber(mContext);
        }

        mScrollNumber.setTextSize(mTextSize);
        mScrollNumber.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
        mScrollNumber.setInterpolator(mInterpolator);
        mScrollNumber.setNumber(val);
        mScrollNumber.setOdomenterInteractionListener( new ScrollNumber.OdomenterInteraction(){
            @Override
            public void onCarry() {
                Log.e("Odometer", "onCarry");
            }

            @Override
            public void onRoundDown() {
                Log.e("Odometer", "onRoundDown");
            }

            @Override
            public void onComplete() {
                Log.e("Odometer", "onComplete");
            }
        });

        addView(mScrollNumber);
    }



    private void resetView() {
        mTargetNumbers.clear();
//        mScrollNumbers.clear();
        removeAllViews();
    }


    public void setNumber(int from, int to) {
//        if (to < from)
//            throw new UnsupportedOperationException("'to' value must > 'from' value");
//
//        resetView();
//        // operate to
//        int number = to, count = 0;
//        while (number > 0) {
//            int i = number % 10;
//            mTargetNumbers.add(i);
//            number /= 10;
//            count++;
//        }
//        // operate from
//        number = from;
//        while (count > 0) {
//            int i = number % 10;
//            mPrimaryNumbers.add(i);
//            number /= 10;
//            count--;
//        }
//
//        for (int i = mTargetNumbers.size() - 1; i >= 0; i--) {
//            ScrollNumber scrollNumber = new ScrollNumber(mContext);
//            scrollNumber.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
//            scrollNumber.setTextSize(mTextSize);
//            if (!TextUtils.isEmpty(mFontFileName))
//                scrollNumber.setTextFont(mFontFileName);
//            scrollNumber.setNumber(mPrimaryNumbers.get(i), mTargetNumbers.get(i), i * 10);
//            mScrollNumbers.add(scrollNumber);
//            addView(scrollNumber);
//        }
        setNumber(to);
    }

//    public void setTextColors(@ColorRes int[] textColors) {
//        mTextColors = textColors;
//        for (int i = mScrollNumbers.size() - 1; i >= 0; i--) {
//            ScrollNumber scrollNumber = mScrollNumbers.get(i);
//            scrollNumber.setTextColor(ContextCompat.getColor(mContext, mTextColors[0]));
//        }
//    }


    public void setTextSize(int textSize) {
//        if (textSize <= 0) throw new IllegalArgumentException("text size must > 0!");
//        mTextSize = textSize;
//        for (ScrollNumber s : mScrollNumbers) {
//            s.setTextSize(textSize);
//        }
        mScrollNumber.setTextSize(textSize);
    }

//    public void setInterpolator(Interpolator interpolator) {
//        if (interpolator == null)
//            throw new IllegalArgumentException("interpolator couldn't be null");
//        mInterpolator = interpolator;
//        for (ScrollNumber s : mScrollNumbers) {
//            s.setInterpolator(interpolator);
//        }
//    }

//    public void setTextFont(String fileName) {
//        if (TextUtils.isEmpty(fileName)) throw new IllegalArgumentException("file name is null");
//        mFontFileName = fileName;
//        for (ScrollNumber s : mScrollNumbers) {
//            s.setTextFont(fileName);
//        }
//    }


    public void adjust(int value) {
        if (0 < value) {
            if ( mScrollNumber != null ){
                mScrollNumber.increase(value);
            }
        } else {
            if ( mScrollNumber != null ){
                mScrollNumber.decrease(-value);
            }
        }
    }

}

