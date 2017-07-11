package com.nicolkill.framework.helpers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nicolkill on 7/10/17.
 */

public final class NetworkHelper extends AbstractHelper {

    private static final String KEY_NETWORK_INFO = "networkInfo";

    private NetworkInfo mNetworkInfo;
    private BroadcastReceiver mBroadcastReceiver;

    private CopyOnWriteArrayList<NetworkListener> mListeners = new CopyOnWriteArrayList<>();

    private static NetworkHelper mInstance;

    public static NetworkHelper getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new NetworkHelper(application);
        }
        return mInstance;
    }

    private NetworkHelper(
            Application application
    ) {
        super(application);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                networkChange((NetworkInfo) intent.getExtras().get(KEY_NETWORK_INFO));
            }
        };
        startListening();
    }

    /**
     * Comienza a la escucha de los cambios de red
     */
    private void startListening() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getApplication().registerReceiver(mBroadcastReceiver, intentFilter);
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkChange(connectivityManager.getActiveNetworkInfo());
    }

    public void addNetworkListener(NetworkListener listener) {
        mListeners.add(listener);
    }

    /**
     * Llamado por el BroadcastReceiver cuando la información de red ha sufrido algún cambio
     */
    private void networkChange(NetworkInfo networkInfo) {
        mNetworkInfo = networkInfo;
        for (NetworkListener listener: mListeners) {
            listener.onConnectionChanged(networkAvailable());
        }
    }

    /**
     * Verifica el estado de la red
     * @return true si la red es valida y esta conectada
     */
    public boolean networkAvailable() {
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }

    /**
     * Verifica si la red es de tipo Wi-fi
     * @return true si la información es válida y si es por Wi-fi
     */
    public boolean isWifi() {
        return mNetworkInfo != null&& mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }


    /**
     * Verifica si la red actual es de datos móviles
     * @return true si la información es válida y si es por datos móviles
     */
    public boolean isMobile() {
        return mNetworkInfo != null && mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public interface NetworkListener {
        void onConnectionChanged(boolean isConnectedOrConnecting);
    }

}
