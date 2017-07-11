package com.nicolkill.framework.interfaces;

import android.app.Application;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.nicolkill.framework.models.ApplicationResponse;


/**
 * Created by Nicol Acosta on 12/16/16.
 * nicol@parkiller.com
 */

public interface ApplicationScreen {

    <ViewType extends View> ViewType findView(@IdRes int id);

    Application getApplication();

    void showMessage(ApplicationResponse response);

    void showLoading(String text);

    void hideLoading();

    void showDebugError(Throwable e);

    FragmentManager getSupportFragmentManager();

}
