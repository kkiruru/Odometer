package com.kkiruru.dataodometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


public class NumberRing extends View {
    private Context mContext;
    private int position = 0;
    private long mDeltaNum = 0;
    private long mCurrNum = 0;
    private long mNextNum = 0;
    private long mTargetNum = 0;
    private long mCarryValue = 0;
    private long mBorrowValue = 0;

    private int direction = 1; // 1 or -1

    private float mOffset;
    private Paint mPaint;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private static final int WIDTH_MARGIN = 0;
    private static final int HEIGHT_MARGIN = 6;
    private int mTextCenterX;
    private int mTextHeight;
    private Rect mTextBounds = new Rect();
    private int mTextSize = 130;
    private int mTextColor = 0x000000;
    private Typeface mTypeface;

    private NumberRingInteraction mNumberRingInteraction;

    public interface NumberRingInteraction {
        void onCarry(int position, long carry);

        void onRoundDown(int position, long borrow);

        void onComplete(int position, long value);
    }


    public NumberRing(Context context) {
        this(context, null);
    }

    public NumberRing(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberRing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mTextColor);

        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-Medium.otf");

        if (mTypeface != null) {
            mPaint.setTypeface(mTypeface);
        }
        mPaint.setTextSize(mTextSize);

        measureTextHeight();
    }

    public void setNumber(final long curr) {
        mCurrNum = Math.abs(curr) % 10;
        mTargetNum = mCurrNum;
        mDeltaNum = 0;
        mOffset = 0;
        removeCallbacks(mScrollRunnable);
        invalidate();
    }

    public void setPositionalNumber(final int position) {
        this.position = position;
    }


    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mPaint.setTextSize(dp2px(mTextSize));
        measureTextHeight();
        requestLayout();
        invalidate();
    }

    public int getTextSize() {
        return this.mTextSize;
    }


    public void setTypeface(Typeface typeface) {
        mTypeface = typeface;
        if (mTypeface == null) {
            throw new RuntimeException("please check your font!");
        }
        mPaint.setTypeface(mTypeface);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mPaint.setColor(mTextColor);
        invalidate();
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    private void measureTextHeight() {
        mPaint.getTextBounds(mCurrNum + "", 0, 1, mTextBounds);
        mTextHeight = mTextBounds.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

        mTextCenterX = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2;
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                mPaint.getTextBounds("0", 0, 1, mTextBounds);
                result = mTextBounds.height();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + dp2px(HEIGHT_MARGIN);
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                mPaint.getTextBounds("0", 0, 1, mTextBounds);
                result = mTextBounds.width();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + dp2px(WIDTH_MARGIN);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrNum != mTargetNum) {
            if (1 <= Math.abs(mOffset)) {
                mOffset = 0;
                calNum(direction);
            }
            postDelayed(mScrollRunnable, 0);
        } else {
            mDeltaNum = 0;
            mOffset = 0;
            if (mNumberRingInteraction != null) {
                mNumberRingInteraction.onComplete(position, mCurrNum);
            }
        }

        canvas.translate(0, mOffset * getMeasuredHeight());
        drawSelf(canvas);
        if (direction == 1) {
            drawNext(canvas);
        } else {
            drawPrev(canvas);
        }
//        canvas.restore();
    }


    private void calNum(int increment) {
        mCurrNum += increment;

        if (mNumberRingInteraction != null) {
            if (direction == 1 && 10 <= mCurrNum) {
                mNumberRingInteraction.onCarry(position, mCarryValue);
                mCarryValue = 0;
                mCurrNum = mCurrNum % 10;
                mTargetNum = mTargetNum % 10;
                mDeltaNum = Math.abs(mTargetNum - mCurrNum);
            } else if (direction == -1 && (mCurrNum < 0 || mCurrNum % 10 == 9)) {
                mNumberRingInteraction.onRoundDown(position, mBorrowValue + 1);
                if (mCurrNum < 0) {
                    mCurrNum = mCurrNum + 10;
//					mNextNum = mCurrNum + increment;
                    mTargetNum = mTargetNum + (Math.abs(mTargetNum) + 9) / 10;
                    mDeltaNum = Math.abs(mTargetNum - mCurrNum);
                }
            }
        }
        mNextNum = mCurrNum + increment;
    }

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            float x = (float) (1 - 1.0 * (mTargetNum - mCurrNum) / mDeltaNum);
            mOffset = mOffset - (float) (0.3f * (1 - mInterpolator.getInterpolation(x) + 0.1) * direction);
            invalidate();
        }
    };


    private void drawNext(Canvas canvas) {
        int y = getMeasuredHeight() * 3 / 2;
        long drawNumber = (Math.abs(mNextNum)) % 10;
        canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2 - HEIGHT_MARGIN, mPaint);
    }


    private void drawSelf(Canvas canvas) {
        int y = getMeasuredHeight() / 2;
        long drawNumber = (Math.abs(mCurrNum)) % 10;
        canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2 - HEIGHT_MARGIN, mPaint);
    }


    private void drawPrev(Canvas canvas) {
        int y = -(getMeasuredHeight() / 2);
        long drawNumber = (Math.abs(mNextNum)) % 10;
        canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2 - HEIGHT_MARGIN, mPaint);
    }


    //현재 값을 num만큼 증가시킨다
    public void increase(long num) {
        Log.d("NumberRing", "increase : " + num);
        num = num + mTargetNum;
        mCurrNum = mTargetNum;
        increaseFromCurrentTo(num);
    }


    //현재 숫자에서 num으로 증가시킨다
    public void increaseFromCurrentTo(long num) {
        Log.d("NumberRing", "increaseFrom : " + mCurrNum + " To : " + num);
        long quotient = num / 10;
        long remainder = (num % 10);

        //현재 값과 변해야 할 값이 같다면, rolling 시킬 필요가 없다
        if (mCurrNum == remainder) {
            mTargetNum = mCurrNum;
            //onCarry 값을 확인한다
            if (0 < quotient) {
                mNumberRingInteraction.onCarry(position, quotient);
            }
            return;
        }

        mCarryValue += quotient;

        //현재 숫자보다 작은 숫자로 변경해야한다면 한 바퀴를 돌아야 한다
        if (remainder < mCurrNum) {
            mTargetNum = remainder + 10;
        } else {
            mTargetNum = remainder;
            //현재 숫자보다 큰 숫자로 변경하지만, 몫이 있다면 한 바퀴를 돌게 하고 onCarry가 발생되게 한다
            if (0 < quotient) {
                mNumberRingInteraction.onCarry(position, mCarryValue);
                mCarryValue = 0;
            }
        }

        direction = 1;
        mOffset = 0;
        mDeltaNum = Math.abs(mTargetNum - mCurrNum);
        invalidate();
    }


    //현재 값을 num만큼 감소시킨다
    public void decrease(long num) {
        Log.d("NumberRing", "decrease : " + num);
        long quotient = num / 10;
        long remainder = (num % 10);

        if ( remainder == 0 ){
            num += mTargetNum;
        }else{

        }
        decreaseFromCurrentTo(num);
    }

    //현재 숫자에서 num으로 감소시킨다
    public void decreaseFromCurrentTo(long num) {
        long quotient = num / 10;
        long remainder = (num % 10);

        //현재 값과 변해야 할 값이 같다면, rolling 시킬 필요가 없다
        if (mCurrNum == remainder) {
            mTargetNum = mCurrNum;
            if (0 < quotient) {
                mNumberRingInteraction.onRoundDown(position, quotient);
            }
            return;
        }

        //현재 숫자보다 큰 숫자로 변경해야한다면 한 바퀴 돌아야한다
        if (mCurrNum < remainder) {
            mCurrNum += 10;
        }
        mTargetNum = remainder;
        mBorrowValue = quotient;

        invalidate();
    }


    public void setOdometerInteractionListener(NumberRingInteraction listener) {
        mNumberRingInteraction = listener;
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public long getCurrentValue() {
        return mCurrNum;
    }

    public long getTargetValue() {
        return mTargetNum;
    }

}
