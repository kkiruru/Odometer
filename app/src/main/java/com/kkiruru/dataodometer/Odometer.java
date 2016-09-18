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

		int number = val;

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
			if (mMeterNumbers.size() <= position + 1) {
				MeterNumber meterNumber = createMeterNumber(position + 1, 0);
				meterNumber.setOdomenterInteractionListener(OdomenterInteraction);
				mMeterNumbers.add(meterNumber);
				addView(meterNumber, 0);
				meterNumber.increase(carry);
			} else {
				mMeterNumbers.get(position + 1).increase(carry);
			}
		}

		@Override
		public void onRoundDown(int position, int borrow) {
			Log.e("Odometer", "onRoundDown");
		}

		@Override
		public void onComplete(int position, int value ) {
			Log.e("Odometer", "onComplete : ["+position+"]" + "_" + value);
			mCurrentValue.setPositionValue(position, value);
				if (mCurrentValue.getValue() == mTargetNumber.getValue()) {
				Log.e("Odometer", "all completed ~~~" + mCurrentValue.getValue() );
			}
		}
	};



	private void resetView() {
		mTargetNumber.clear();
		mMeterNumbers.clear();
		removeAllViews();
	}


	public void adjust(int value) {

		Log.d("adjust", mTargetNumber.getValue() + " > " + ( mTargetNumber.getValue() + value));

		mTargetNumber.setValue(mTargetNumber.getValue() + value);

		PositionalNumber adjustValue = new PositionalNumber(value);

		if (0 < value) {
			int increment = 0;
			for (int i = adjustValue.size() - 1; 0 <= i ; i--) {
				increment = increment*10 + adjustValue.getPositionValue(i);
				if (i <= mMeterNumbers.size() - 1 ) {
					mMeterNumbers.get(i).increase(increment);
					increment = 0;
				}
			}
		} else {
			for (int i = 0; i <= mTargetNumber.size() - 1; i++) {
				if (mMeterNumbers.get(i) != null) {
					mMeterNumbers.get(i).decrease(adjustValue.getPositionValue(i));
				}
			}
		}
	}

}

