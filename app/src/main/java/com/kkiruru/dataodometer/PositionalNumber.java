package com.kkiruru.dataodometer;

import android.util.Log;

/**
 * Created by kkiruru on 2016. 9. 17..
 */

public class PositionalNumber {
	private long mValue = 0;

	public PositionalNumber(){
		this(0);
	}

	public PositionalNumber(long value){
		mValue = value;
	}

	public void setValue(long value){
		mValue = value;
	}

	public long getValue(){
		return mValue;
	}

	public void addValue(long value){
		mValue += value;
	}


	public void setPositionValue(int position, long value){
		if( 10 <= value ){
			return;
		}

		long old = getPositionValue(position);
		if ( value == old ){
			return;
		}

		mValue = mValue - ( getPositionValue(position) * (int)Math.pow(10, position));
		mValue = mValue + ( value * (int)Math.pow(10, position));
	}


	public long getPositionValue(int position){
		long value = (mValue / (int)Math.pow(10, position)) % 10;
		return value;
	}


	public int size(){
		int length = 0;
		long value = mValue;

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
