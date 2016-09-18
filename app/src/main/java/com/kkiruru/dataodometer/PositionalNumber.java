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
		while( mNumbers.size() -1 <= position ){
			mNumbers.add(0);
		}
		mNumbers.set(position, value);
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
