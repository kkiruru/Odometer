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

/**
 * Created by wuhaojie on 2016/7/15 11:36.
 */
public class MeterNumber extends View {
	private Context mContext;
	private int position = 0;
	private int mDeltaNum = 0;
	private int mCurrNum = 0;
	private int mNextNum = 0;
	private int mTargetNum = 0;

	private int direction = 1; // 1 or -1

	private float mOffset;
	private Paint mPaint;
	private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

	private static final int MARGIN = 2;
	private int mTextCenterX;
	private int mTextHeight;
	private Rect mTextBounds = new Rect();
	private int mTextSize = dp2px(15);
	private int mTextColor = 0x000000;
	private Typeface mTypeface;

	private OdomenterInteraction mOdomenterInteraction;

	interface OdomenterInteraction {
		void onCarry(int position, int carry);

		void onRoundDown(int position, int borrow);

		void onComplete(int position, int value);
	}


	public MeterNumber(Context context) {
		this(context, null);
	}

	public MeterNumber(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MeterNumber(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mContext = context;

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);

		if (mTypeface != null) mPaint.setTypeface(mTypeface);

		measureTextHeight();
	}

	public void setNumber(final int curr) {
		mCurrNum = Math.abs(curr) % 10;
		mTargetNum = mCurrNum;
		mDeltaNum = 0;
		mOffset = 0;
		invalidate();
	}

	public void setPositionalNumber(final int position) {
		this.position = position;
	}


	public void setTextSize(int textSize) {
		this.mTextSize = dp2px(textSize);
		mPaint.setTextSize(mTextSize);
		measureTextHeight();
		requestLayout();
		invalidate();
	}


	public void setTypeface(Typeface typeface) {
		mTypeface = typeface;
		if (mTypeface == null) throw new RuntimeException("please check your font!");
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
		return result + dp2px(MARGIN);
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
		return result + dp2px(MARGIN);
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
			if (mOdomenterInteraction != null) {
				mOdomenterInteraction.onComplete(position, mCurrNum);
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
		mNextNum = mCurrNum + increment;

		Log.d("calNum", "[" + position +"], mCurrNum : " + mCurrNum + ", mTargetNum : " + mTargetNum);

		if (mOdomenterInteraction != null) {
			if( direction == 1 && 10 <= mCurrNum ){
				mOdomenterInteraction.onCarry(position, mTargetNum/10);

				mCurrNum = mCurrNum % 10;
				mNextNum = mCurrNum + increment;
				mTargetNum = mTargetNum % 10;
				mDeltaNum = Math.abs(mTargetNum - mCurrNum);
			} else if (direction == -1 && mCurrNum < 0 ) {
				mOdomenterInteraction.onRoundDown(position, (Math.abs(mTargetNum) + 9 ) / 10);

				mCurrNum = mCurrNum + 10;
				mNextNum = mCurrNum + increment;
				mTargetNum = mTargetNum + 10;

				mTargetNum = mTargetNum + ( Math.abs(mTargetNum) + 9 ) / 10;
				mDeltaNum = Math.abs(mTargetNum - mCurrNum);
			}
		}
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
		int drawNumber = (Math.abs(mNextNum)) % 10;
		canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2, mPaint);
	}


	private void drawSelf(Canvas canvas) {
		int y = getMeasuredHeight() / 2;
		int drawNumber = (Math.abs(mCurrNum)) % 10;
		canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2, mPaint);
	}


	private void drawPrev(Canvas canvas) {
		int y = -(getMeasuredHeight() / 2);
		int drawNumber = (Math.abs(mNextNum)) % 10;
		canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2, mPaint);
	}


	public void increase(int num) {
		Log.d("increase", "[" + position +"], target " + mTargetNum + " > " + (mTargetNum + num));
		mTargetNum = mTargetNum + num;
		direction = 1;
		mOffset = 0;
		mDeltaNum = Math.abs(mTargetNum - mCurrNum);
		invalidate();
	}

	public void decrease(int num) {
		Log.d("decrease", "[" + position +"], target " + mTargetNum + " > " + (mTargetNum - num));
		if (num <= 0) {
			return;
		}
		mTargetNum -= num;
		direction = -1;
		mOffset = 0;
		mDeltaNum = Math.abs(mTargetNum - mCurrNum);
		invalidate();
	}

	public void setOdomenterInteractionListener(OdomenterInteraction listener) {
		mOdomenterInteraction = listener;
	}

	private int dp2px(float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}


}
