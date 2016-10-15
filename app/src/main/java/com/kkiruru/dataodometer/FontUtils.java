package com.kkiruru.dataodometer;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by 1100416 on 2016. 10. 14..
 */

public class FontUtils {
    private Context mContext;

    private static Typeface mHelveticaNeue = null;

    public FontUtils(Context context){
        mContext = context;
    }

    public static Typeface helveticaNeue(Context context){
        if ( mHelveticaNeue == null ){
            mHelveticaNeue = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-Medium.otf");
        }
        return mHelveticaNeue;
    }
}
