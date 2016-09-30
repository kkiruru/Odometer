package com.kkiruru.dataodometer;

import android.util.Log;

/**
 * Created by kkiruru on 2016. 9. 17..
 */

public class PositionalNumber {
	private int mValue = 0;

	public PositionalNumber(){
		this(0);
	}

	public PositionalNumber(int value){
		mValue = value;
	}

	public void setValue(int value){
		mValue = value;
	}

	public int getValue(){
		return mValue;
	}

	public void setPositionValue(int position, int value){
		if( 10 <= value ){
			return;
		}

		int old = getPositionValue(position);
		if ( value == old ){
			return;
		}

		mValue = mValue - ( getPositionValue(position) * (int)Math.pow(10, position));
		mValue = mValue + ( value * (int)Math.pow(10, position));

		Log.d("PositionalNumber", "setPositionValue["+position+"] : " + old + " > " + getPositionValue(position));
	}


	public int getPositionValue(int position){
		int value = (mValue / (int)Math.pow(10, position)) % 10;
//		Log.d("PositionalNumber", "getPositionValue["+position+"] : " + value);
		return value;
	}


	public int size(){
		int length = 0;
		int value = mValue;

		if (value == 0 ){
			return 1;
		}

		while( 0 < value ){
			value = value / 10;
			length++;
		}

		return length;
	}

	public void clear(){
		mValue = 0;
	}

}
