package com.nicolkill.framework.util;

import android.content.Context;

/**
 * Created by nicolkill on 6/23/17.
 */

public class SizeUtils {

    /**
     * Transforma dp a pixeles
     * @param context contexto android
     * @param dp dp
     * @return dp transformado a pixeles
     */
    public static int paddingDpTransform(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density + 0.5f);
    }

}
