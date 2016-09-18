package com.kkiruru.dataodometer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkiruru on 2016. 9. 17..
 */

public class PositionalNumber {
	private List<Integer> mNumbers = new ArrayList<>();

	public PositionalNumber(){

	}

	public PositionalNumber(int value){
		setValue(value);
	}

	public void setValue( int value ){
		clear();
		do {
			int i = value % 10;
			mNumbers.add(i);
			value /= 10;
		} while (value > 0);
	}

	public void setPositionValue(int position, int value){
		value = getValue() + position == 0 ? value : 10^position * value;
		setValue(value);
	}


	public int getPositionValue(int position){
		if ( position < mNumbers.size() ){
			return mNumbers.get(position);
		}else{
			return 0;
		}
	}


	public int getValue(){
		int retValue = 0;
		for( int i = mNumbers.size() - 1; 0 <= i ; i-- ){
			retValue = retValue * 10 + mNumbers.get(i);
		}
		return retValue;
	}


	public int size(){
		return mNumbers.size();
	}

	public void clear(){
		mNumbers.clear();
	}

}
