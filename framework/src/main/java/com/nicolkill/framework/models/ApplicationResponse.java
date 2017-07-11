package com.nicolkill.framework.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;


import com.nicolkill.framework.R;
import com.nicolkill.framework.exceptions.NetworkException;
import com.nicolkill.framework.exceptions.PermissionException;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Modelo de una respuesta entre un PresenterClass y una actividad
 *
 * Las respuestas contienen un mensaje, acciones con listeners de acción y acción al cerrado
 *
 * Las respuestas entre estos pueden ser tan complejas como lo requieran
 *
 * Created by Nicol Acosta on 10/10/16.
 * nicol@parkiller.com
 */
public class ApplicationResponse implements Serializable {

    private static final String TAG = ApplicationResponse.class.getSimpleName();

    private static final String PACKAGE = "package";

    private String mMessage;
    private HashMap<String, OnClickListener> mActions;
    private OnDismissListener mOnDismissListener;

    /**
     * Obtiene el mensaje a mostrar
     * @return mensaje
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Obtiene las acciones con listeners de acción
     * @return acciones con listeners de acción
     */
    public HashMap<String, OnClickListener> getActions() {
        return mActions;
    }

    /**
     * Obtiene el listener de acción de cerrado
     * @return acción
     */
    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    /**
     * Clase Builder utilizada para la creación de instancias de esta clase
     */
    public static class Builder {

        private ApplicationResponse mResponse;
        private Context mContext;

        public Builder(Context context, Throwable e) {
            this(context, e.getMessage());
            if (e instanceof NetworkException) {
                setMessage(mContext.getString(R.string.warning_internet));
                addAction(mContext.getString(R.string.enable_wifi), new OnClickListener() {
                    @Override
                    public void onClick(ApplicationResponse response, View view) {
                        mContext.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
                addAction(mContext.getString(R.string.enable_mobile_data), new OnClickListener() {
                    @Override
                    public void onClick(ApplicationResponse response, View view) {
                        mContext.startActivity(
                                new Intent()
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setAction(Settings.ACTION_DATA_ROAMING_SETTINGS)
                        );
                    }
                });
            }
            if (e instanceof PermissionException) {
                addAction(mContext.getString(R.string.open_permissions), new OnClickListener() {
                    @Override
                    public void onClick(ApplicationResponse response, View view) {
                        mContext.startActivity(
                                new Intent()
                                        .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts(PACKAGE, mContext.getPackageName(), null))
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                    }
                });
            }
            if (e instanceof UnknownHostException) {
                setMessage(mContext.getString(R.string.warning_internet_error));
                addAction(mContext.getString(R.string.enable_wifi), new OnClickListener() {
                    @Override
                    public void onClick(ApplicationResponse response, View view) {
                        mContext.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
                addAction(mContext.getString(R.string.enable_mobile_data), new OnClickListener() {
                    @Override
                    public void onClick(ApplicationResponse response, View view) {
                        mContext.startActivity(
                                new Intent()
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setAction(Settings.ACTION_DATA_ROAMING_SETTINGS)
                        );
                    }
                });
            }
        }

        public Builder(Context context, String message) {
            mContext = context;
            mResponse = new ApplicationResponse();
            mResponse.mMessage = message;
            mResponse.mActions = new HashMap<>();
        }

        /**
         * Cambia el mensaje de la instancia ApplicationResponse
         * @param message mensaje
         * @return instancia de ApplicationResponse.Builder
         */
        public Builder setMessage(String message) {
            mResponse.mMessage = message;
            return this;
        }

        /**
         * Agrega una acción a la instancia ApplicationResponse
         * @param message acción
         * @param onClickListener listener
         * @return instancia de ApplicationResponse.Builder
         */
        public Builder addAction(String message, OnClickListener onClickListener) {
            mResponse.mActions.put(message, onClickListener);
            return this;
        }

        /**
         * Agrega a la instancia de ApplicationResponse un dismiss listener (listener de cerrado)
         * @param onDismissListener listener
         * @return instancia de ApplicationResponse.Builder
         */
        public Builder addDissmissAction(OnDismissListener onDismissListener) {
            mResponse.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Devuelve la instancia ApplicationResponse
         * @return instancia ApplicationResponse
         */
        public ApplicationResponse build() {
            mResponse.mActions.put(mContext.getString(R.string.close), null);
            return mResponse;
        }

    }

    public interface OnClickListener {
        void onClick(ApplicationResponse response, View view);
    }

    public interface OnDismissListener {
        void onDismiss(ApplicationResponse response);
    }

}
