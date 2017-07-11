package com.nicolkill.framework.mvp;

import android.app.Application;
import android.support.annotation.StringRes;

import com.nicolkill.framework.helpers.AbstractHelper;
import com.nicolkill.framework.models.ApplicationResponse;

/**
 * Padre de los Presenters que tendrán las aplicaciones Parkiller
 *
 * Created by Nicol Acosta on 10/10/16.
 * nicol@parkiller.com
 */
public abstract class Presenter<T extends PresenterView> extends AbstractHelper {

    private static final String TAG = Presenter.class.getSimpleName();

    private T mView;

    public Presenter(
            Application application,
            T view
    ) {
        super(application);
        mView = view;
    }

    protected final T getView() {
        return mView;
    }

    public void showMessage(ApplicationResponse response) {
        mView.showMessage(response);
    }

    public void showLoading(String text) {
        mView.showLoading(text);
    }

    public void hideLoading() {
        mView.hideLoading();
    }

    public final void showDebugError(Throwable e) {
        mView.showDebugError(e);
    }

    public String getString(@StringRes int resString) {
        return getApplication().getString(resString);
    }

    public String getString(@StringRes int resString, Object... formatArgs) {
        return getApplication().getString(resString, formatArgs);
    }

}
