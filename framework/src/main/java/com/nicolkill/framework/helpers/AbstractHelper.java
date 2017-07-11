package com.nicolkill.framework.helpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nicolkill on 7/10/17.
 */

public abstract class AbstractHelper {

    private Application mApplication;
    private SharedPreferences mSharedPreferences;

    public AbstractHelper(Application application) {
        mApplication = application;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * Devuelve el contexto Android que incluye funciones para obtener cualquier recurso del sistema
     * @return instancia de Application generada por android
     * @see Application
     */
    public Application getApplication() {
        return mApplication;
    }

    /**
     * Devuelve el objeto SharedPreferences el cual ayuda a almacenar datos para usos futuros
     * @return instancia de SharedPreferences generada por Application
     * @see SharedPreferences
     */
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
