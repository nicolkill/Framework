package com.nicolkill.framework.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicolkill.framework.models.ApplicationResponse;


/**
 * Clase DialogFragment que se encarga de mostrar en un diálogo las instancias de la clase ApplicationResponse
 *
 * @see ApplicationResponse
 *
 * Created by Nicol Acosta on 10/19/16.
 * nicol@parkiller.com
 */
public class ParkillerMultiactionDialog extends DialogFragment implements MultiOptionView.MultiOptionDataCollecter {

    private static final String TAG = ParkillerMultiactionDialog.class.getSimpleName();

    private static final String KEY_RESPONSE = "response";

    private ApplicationResponse mResponse;

    /**
     * Llamado cuando se desea obtener una instancia de este diálogo
     * @param response instancia de ApplicationResponse que mostrará
     * @return instancia del diálogo
     */
    public static ParkillerMultiactionDialog newInstance(ApplicationResponse response) {
        ParkillerMultiactionDialog bottomSheet = new ParkillerMultiactionDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_RESPONSE, response);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    /**
     * Llamado por el sistema cuando se desea crear la vista de el diálogo
     * @param inflater inflador de vistas
     * @param container contenedor de la vista actual
     * @param savedInstanceState instancia de datos enviados por el sistema para crear la vista
     * @return vista creada
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mResponse = (ApplicationResponse) getArguments().getSerializable(KEY_RESPONSE);
        return new MultiOptionView(getContext()).setDataCollecter(this);
    }

    /**
     * Llamado por el sistema cuando se está cerrando el diálogo
     *
     * Permite ejecutar una acción cuando se cierra el diálogo
     *
     * @param dialog diálogo que se está cerrando
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ApplicationResponse.OnDismissListener dismissListener = mResponse.getOnDismissListener();
        if (dismissListener != null) {
            dismissListener.onDismiss(mResponse);
        }
    }

    /**
     * Llama a la función dismiss para que una clase remota pueda cerrar este diálogo
     */
    @Override
    public void close() {
        dismiss();
    }

    @Override
    public ApplicationResponse getData() {
        return mResponse;
    }

}
