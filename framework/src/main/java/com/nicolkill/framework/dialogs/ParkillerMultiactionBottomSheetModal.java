package com.nicolkill.framework.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;

import com.nicolkill.framework.models.ApplicationResponse;

/**
 * Clase BottomSheetDialog que se encarga de mostrar en un bottom sheet las instancias de la clase ApplicationResponse
 *
 * @see ApplicationResponse
 *
 * Created by Nicol Acosta on 10/13/16.
 * nicol@parkiller.com
 */
public class ParkillerMultiactionBottomSheetModal extends BottomSheetDialog implements MultiOptionView.MultiOptionDataCollecter {

    private ApplicationResponse mResponse;

    /**
     * Llamado cuando se desea obtener una instancia de este bottom sheet
     * @param context contexto Android necesario para funciones del sistema
     * @param response instancia de ApplicationResponse que mostrará
     * @return instancia de bottom sheet
     */
    public static ParkillerMultiactionBottomSheetModal newInstance(Context context, ApplicationResponse response) {
        ParkillerMultiactionBottomSheetModal bottomSheet = new ParkillerMultiactionBottomSheetModal(context, response);
        return bottomSheet;
    }

    private ParkillerMultiactionBottomSheetModal(@NonNull Context context, ApplicationResponse response) {
        super(context);
        mResponse = response;
        init();
    }

    /**
     * Inicializa la vista y la agrega al bottom sheet
     */
    private void init() {
        setContentView(
                new MultiOptionView(getContext())
                        .setDataCollecter(this)
        );
    }

    /**
     * Muestra el bottom sheet y le agrega un dismiss listener para ejecutar una función determinada al cerrar el bottom sheet
     */
    @Override
    public void show() {
        super.show();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mResponse.getOnDismissListener() != null) {
                    mResponse.getOnDismissListener().onDismiss(mResponse);
                }
            }
        });
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