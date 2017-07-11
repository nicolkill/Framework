package com.nicolkill.framework.result.base;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.nicolkill.framework.interfaces.CallbackResponse;

/**
 * Created by nicolkill on 7/3/17.
 */

public abstract class ResultExecuter<T> {

    protected int mRequestCode;
    protected CallbackResponse<T> mCallbackResponse;

    public ResultExecuter setCallbackResponse(CallbackResponse<T> callbackResponse) {
        mCallbackResponse = callbackResponse;
        return this;
    }

    public ResultExecuter setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public abstract void execute(
            ShadowSupportFragment fragment,
            int requestCode
    );

    public abstract void onActivityResult(
            ShadowSupportFragment fragment,
            int requestCode,
            int resultCode,
            Intent data
    );

    public abstract void onRequestPermissionsResult(
            ShadowSupportFragment fragment,
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    );

}
