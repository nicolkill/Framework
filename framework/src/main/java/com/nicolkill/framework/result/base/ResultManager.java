package com.nicolkill.framework.result.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.interfaces.CallbackResponse;

import java.security.InvalidParameterException;

/**
 * Created by nicolkill on 7/3/17.
 */

public abstract class ResultManager<T> implements CallbackResponse<T> {

    private FragmentManager mFragmentManager;
    protected CallbackResponse<T> mCallbackResponse;

    public ResultManager(ApplicationScreen screen) {
        this(screen.getSupportFragmentManager());
    }

    public ResultManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public final ResultManager setCallback(@NonNull CallbackResponse<T> callbackResponse) {
        mCallbackResponse = callbackResponse;
        return this;
    }

    public final void request(@IntRange(from = 1, to = Integer.MAX_VALUE) int requestCode) {
        if (mCallbackResponse == null) {
            throw new InvalidParameterException("Callback must be set");
        }
        addFragment(
                mFragmentManager,
                ShadowSupportFragment.getInstance(
                        requestCode,
                        getResultExecuter()
                                .setCallbackResponse(this)
                                .setRequestCode(requestCode)
                )
        );
    }

    public abstract ResultExecuter<T> getResultExecuter();

    @Override
    public final void sendValue(T response) {
        removeFragment();
        mCallbackResponse.sendValue(response);
    }

    @Override
    public final void error(Exception e) {
        removeFragment();
        mCallbackResponse.error(e);
    }

    private void removeFragment() {
        if (mFragmentManager != null) {
            removeFragment(mFragmentManager);
        }
    }

    private static final String PERMISSION_TAG = "permission";

    private void removeFragment(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(PERMISSION_TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void addFragment(FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager == null) {
            return;
        }
        if (fragment == null) {
            return;
        }
        fragmentManager.beginTransaction()
                .add(fragment, PERMISSION_TAG)
                .commit();
    }

}
