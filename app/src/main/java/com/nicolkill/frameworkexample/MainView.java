package com.nicolkill.frameworkexample;

import android.view.View;

import com.nicolkill.framework.mvp.PresenterView;

/**
 * Created by nicolkill on 7/5/17.
 */

public interface MainView extends PresenterView<MainActivity> {

    void presenterClick(View view);

    void presenterLongClick(View view);

}
