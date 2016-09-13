package com.kkiruru.dataodometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by wuhaojie on 2016/7/15 11:36.
 */
public class ScrollNumber extends View {
	private Context mContext;

	private int mDeltaNum = 0;
	private int mCurNum = 0;
	private int mNextNum = 0;
	private int mTargetNum = 0;

	private int direction = 1; // 1 or -1

	private float mOffset;
	private Paint mPaint;
	private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

	private int mTextCenterX;
	private int mTextHeight;
	private Rect mTextBounds = new Rect();
	private int mTextSize = dp2px(90);
	private int mTextColor = 0xf6337b;
	private Typeface mTypeface;

	private OdomenterInteraction mOdomenterInteraction;

	interface OdomenterInteraction {
		void onCarry();

		void onRoundDown();

		void onComplete();
	}


	public ScrollNumber(Context context) {
		this(context, null);
	}

	public ScrollNumber(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollNumber(Context context, AttributeSet attrs, int defStyleAttr) {
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
		setFromNumber(curr);
	}


	public void setTextSize(int textSize) {
		this.mTextSize = dp2px(textSize);
		mPaint.setTextSize(mTextSize);
		measureTextHeight();
		requestLayout();
		invalidate();
	}


	public void setTextFont(String fileName) {
		if (TextUtils.isEmpty(fileName))
			throw new IllegalArgumentException("please check file name end with '.ttf' or '.otf'");
		mTypeface = Typeface.createFromAsset(mContext.getAssets(), fileName);
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
		mPaint.getTextBounds(mCurNum + "", 0, 1, mTextBounds);
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

//        return result + getPaddingTop() + getPaddingBottom()+dp2px(40);
		return result + dp2px(2);
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
//        return result + getPaddingLeft() + getPaddingRight() + 15;
		return result + dp2px(2);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		if (mCurNum != mTargetNum) {
			if (1 <= Math.abs(mOffset)) {
				mOffset = 0;
				calNum(mCurNum + direction);
			}
			postDelayed(mScrollRunnable, 0);
		} else {
			rearrange();
			mOffset = 0;
			if (mOdomenterInteraction != null) {
				mOdomenterInteraction.onComplete();
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


	private void rearrange() {
		mCurNum = Math.abs(mCurNum) % 10;
		mTargetNum = mCurNum;
		mDeltaNum = Math.abs(mTargetNum - mCurNum);
	}


	private void setFromNumber(int number) {
		mCurNum = Math.abs(number) % 10;
		mOffset = 0;
		invalidate();
	}


	private void setTargetNumber(int nextNum) {
		this.mTargetNum = nextNum;
		invalidate();
	}


	private void calNum(int number) {
		mCurNum = number;
		mNextNum = number + direction;
		if (mOdomenterInteraction != null) {
			if (mCurNum % 10 == 0 && direction == 1) {
				mOdomenterInteraction.onCarry();
			} else if (direction == -1 && (mCurNum == -1 || mCurNum % 10 == 9)) {
				mOdomenterInteraction.onRoundDown();
			}
		}
	}


	private Runnable mScrollRunnable = new Runnable() {
		@Override
		public void run() {
			float x = (float) (1 - 1.0 * (mTargetNum - mCurNum) / mDeltaNum);
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
		int drawNumber = (Math.abs(mCurNum)) % 10;
		canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2, mPaint);
	}


	private void drawPrev(Canvas canvas) {
		int y = -(getMeasuredHeight() / 2);
		int drawNumber = (Math.abs(mNextNum)) % 10;
		canvas.drawText(drawNumber + "", mTextCenterX, y + mTextHeight / 2, mPaint);
	}


	private int dp2px(float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

//	public int getTargetNumber() {
//		return this.mTargetNum;
//	}

	public void increase(int num) {
		mTargetNum = mTargetNum + num;
		direction = 1;
		mOffset = 0;
		mDeltaNum = Math.abs(mTargetNum - mCurNum);
		invalidate();
	}

	public void decrease(int num) {
		if ( num <= 0 ){
			return;
		}

		if ( mTargetNum - num < 0 ){
			mCurNum += (( num + 9 ) / 10 ) * 10;
			mTargetNum = mCurNum;
		}

		mTargetNum -= num;
		direction = -1;
		mOffset = 0;
		mDeltaNum = Math.abs(mTargetNum - mCurNum);
		invalidate();
	}

	public void setOdomenterInteractionListener(OdomenterInteraction listener) {
		mOdomenterInteraction = listener;
	}

}
