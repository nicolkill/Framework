package com.nicolkill.framework.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.nicolkill.framework.R;
import com.nicolkill.framework.dialogs.ParkillerMultiactionBottomSheetModal;
import com.nicolkill.framework.dialogs.ParkillerMultiactionDialog;
import com.nicolkill.framework.exceptions.NetworkException;
import com.nicolkill.framework.extractor.Extractor;
import com.nicolkill.framework.extractor.presenter.PresenterLifecycle;
import com.nicolkill.framework.helpers.NetworkHelper;
import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.models.ApplicationResponse;
import com.nicolkill.framework.modules.LogErrorsModule;
import com.nicolkill.framework.mvp.Presenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Created by nicolkill on 6/1/17.
 */

public class BaseActivity<P extends Presenter> extends AppCompatActivity implements ApplicationScreen, NetworkHelper.NetworkListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int MIN_ACTIONS = 4;

    private int mMinActions = MIN_ACTIONS;

    private boolean mFistLaunch = true;
    private P mPresenter;
    private HashMap<PresenterLifecycle.Event, Method> mLifecycleEvents;

    private ProgressDialog mProgressDialog;
    private Snackbar mSnackbar;

    public <ViewType extends View> ViewType findView(@IdRes int id) {
        return (ViewType) findViewById(id);
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected P createPresenter() {
        return (P) Extractor.createPresenter(this);
    }

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        NetworkHelper.getInstance(getApplication())
                .addNetworkListener(this);

        mPresenter = createPresenter();
        mLifecycleEvents = Extractor.getLifecycleEvents(mPresenter);

        try {
            setContentView(Extractor.getScreenLayout(this));
            Extractor.bindFields(this);
            Extractor.bindMethods(this);
        } catch (IllegalAccessException e) {
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e) {
            showDebugError(e);
        }
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_START);
    }

    @Override
    public final void setContentView(int viewId) {
        super.setContentView(viewId);
        hideKeyboard();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
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

    @Override
    protected void onPause() {
        super.onPause();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_ANY);
        invokeLifecycleMethod(PresenterLifecycle.Event.ON_DESTROY);
        Runtime.getRuntime().gc();
        System.gc();
    }

    public final void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showMessage(ApplicationResponse response) {
        if (response.getActions().size() < mMinActions) {
            ParkillerMultiactionDialog.newInstance(response).show(getSupportFragmentManager(), TAG);
        } else {
            ParkillerMultiactionBottomSheetModal.newInstance(this, response).show();
        }
    }

    @Override
    public void showLoading(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getString(R.string.loading));
        }
        if (!mProgressDialog.isShowing()) {
            if (text != null) {
                mProgressDialog.setMessage(text);
            }
            mProgressDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showDebugError(Throwable e) {
        LogErrorsModule.run(e);
    }

    protected void setMinActionsToShowDialog(int number) {
        mMinActions = number;
    }

    @Override
    public void onConnectionChanged(boolean isConnectedOrConnecting) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(
                    getWindow().getDecorView(),
                    R.string.network_not_avalilable,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.options, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMessage(new ApplicationResponse.Builder(BaseActivity.this, new NetworkException()).build());
                }
            });
        }
        if (isConnectedOrConnecting) {
            mSnackbar.dismiss();
        } else {
            mSnackbar.show();
        }
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
