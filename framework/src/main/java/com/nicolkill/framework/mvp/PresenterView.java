package com.nicolkill.framework.mvp;

import com.nicolkill.framework.interfaces.ApplicationScreen;
import com.nicolkill.framework.models.ApplicationResponse;

/**
 * Padre de las interfaces View que tendrán las aplicaciones Parkiller
 *
 * Created by Nicol Acosta on 10/10/16.
 * nicol@parkiller.com
 */
public interface PresenterView<V extends ApplicationScreen> {

    void showMessage(ApplicationResponse response);

    void showLoading(String text);

    void hideLoading();

    void showDebugError(Throwable e);

}
