package com.nicolkill.framework.result.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Created by nicolkill on 6/23/17.
 */

public final class ShadowSupportFragment extends Fragment {

    private int mRequestCode;
    private ResultExecuter mResultExecuter;

    public static ShadowSupportFragment getInstance(
            int requestCode,
            ResultExecuter resultExecuter
    ) {
        ShadowSupportFragment shadowFragment = new ShadowSupportFragment();
        shadowFragment.mRequestCode = requestCode;
        shadowFragment.mResultExecuter = resultExecuter;
        return shadowFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResultExecuter.execute(this, mRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mResultExecuter.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mResultExecuter.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void requestPermission(@NonNull String[] permissions, int requestCode) {
        super.requestPermissions(permissions, requestCode);
    }

    public boolean shouldShowPermissionRationale(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
    }

}
