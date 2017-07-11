package com.nicolkill.frameworkexample;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.nicolkill.framework.extractor.presenter.PresenterLifecycle;
import com.nicolkill.framework.mvp.Presenter;

/**
 * Created by nicolkill on 7/5/17.
 */

public class MainPresenter extends Presenter<MainView> {

    public MainPresenter(Application application, MainView view) {
        super(application, view);
    }

    public void click(View view) {
        getView().presenterClick(view);
    }

    public void longClick(View view) {
        getView().presenterLongClick(view);
    }

}
