package com.nicolkill.framework.result;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.result.base.ResultExecuter;
import com.nicolkill.framework.result.base.ResultManager;
import com.nicolkill.framework.result.base.ShadowSupportFragment;

import java.util.HashMap;

/**
 * Created by nicolkill on 7/3/17.
 */

public class ActivityResultManager extends ResultManager<HashMap<String, Object>> {

    public static final String REQUEST_CODE = "request_code";
    public static final String RESULT_CODE = "result_code";
    public static final String DATA = "data";

    private Intent mIntent;

    public ActivityResultManager(ApplicationScreen screen, Intent intent) {
        super(screen);
        mIntent = intent;
    }

    @Override
    public ResultExecuter<HashMap<String, Object>> getResultExecuter() {
        return new ActivityResultExecuter();
    }

    private class ActivityResultExecuter extends ResultExecuter<HashMap<String, Object>> {

        @Override
        public void execute(ShadowSupportFragment fragment, int requestCode) {
            fragment.startActivityForResult(
                    mIntent,
                    requestCode
            );
        }

        @Override
        public void onActivityResult(ShadowSupportFragment fragment, int requestCode, int resultCode, Intent data) {
            if (requestCode == mRequestCode) {
                HashMap<String, Object> dataToSend = new HashMap<>();
                dataToSend.put(REQUEST_CODE, requestCode);
                dataToSend.put(RESULT_CODE, resultCode);
                dataToSend.put(DATA, data);
                mCallbackResponse.sendValue(dataToSend);
            }
        }

        @Override
        public void onRequestPermissionsResult(ShadowSupportFragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        }

    }

}
