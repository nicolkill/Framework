package com.nicolkill.framework.fragment;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicolkill.framework.extractor.presenter.PresenterLifecycle;
import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.activity.BaseActivity;
import com.nicolkill.framework.models.ApplicationResponse;
import com.nicolkill.framework.mvp.Presenter;
import com.nicolkill.framework.extractor.Extractor;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by nicolkill on 6/1/17.
 */

public class BaseFragment<P extends Presenter> extends Fragment implements ApplicationScreen {

    private static final String TAG = BaseFragment.class.getSimpleName();

    private boolean mFistLaunch = true;
    private P mPresenter;
    private HashMap<PresenterLifecycle.Event, Method> mLifecycleEvents;

    public <ViewType extends View> ViewType findView(@IdRes int id) {
        return (ViewType) getView().findViewById(id);
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected P createPresenter() {
        return (P) Extractor.createPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mLifecycleEvents = Extractor.getLifecycleEvents(mPresenter);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int res;
        try {
            res = Extractor.getScreenLayout(this);
        } catch (Exception e) {
            res = getScreenResource();
        }
        if (res != -1) {
            return inflater.inflate(res, container, false);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Extractor.bindFields(this);
            Extractor.bindMethods(this);
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
            showDebugError(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_START);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFistLaunch) {
            onFirstLaunch();
            mFistLaunch = false;
        }
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_RESUME);
    }

    protected void onFirstLaunch() {
    }

    public int getScreenResource() {
        return -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_STOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_DESTROY);
        Runtime.getRuntime().gc();
        System.gc();
    }

    public final BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public Application getApplication() {
        return getActivity().getApplication();
    }

    @Override
    public void showMessage(ApplicationResponse response) {
        if (getBaseActivity() != null) {
            getBaseActivity().showMessage(response);
        }
    }

    @Override
    public void showLoading(String text) {
        if (getBaseActivity() != null) {
            getBaseActivity().showLoading(text);
        }
    }

    @Override
    public void hideLoading() {
        if (getBaseActivity() != null) {
            getBaseActivity().hideLoading();
        }
    }

    @Override
    public void showDebugError(Throwable e) {
        if (getBaseActivity() != null) {
            getBaseActivity().showDebugError(e);
        }
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
    }

    private void invokeLifecycleMethod(PresenterLifecycle.Event event) {
        try {
            Method method = mLifecycleEvents.get(event);
            if (method != null) {
                method.invoke(mPresenter);
            }
        } catch (Exception e) {
        }
    }

}
