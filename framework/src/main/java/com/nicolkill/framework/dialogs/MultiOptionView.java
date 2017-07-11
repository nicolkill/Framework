package com.nicolkill.framework.dialogs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolkill.framework.R;
import com.nicolkill.framework.models.ApplicationResponse;
import com.nicolkill.framework.util.SizeUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nicolkill on 6/23/17.
 */

public class MultiOptionView extends LinearLayout {

    private TextView mMessage;
    private RecyclerView mRecyclerView;

    public MultiOptionView(Context context) {
        super(context);
        createView();
    }

    public MultiOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOrientation(LinearLayout.VERTICAL);
        mMessage = new TextView(getContext());
        int paddingDp = SizeUtils.paddingDpTransform(getContext(), 16);
        mMessage.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        mMessage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mMessage.setTextSize(18f);
        mMessage.setBackgroundColor(getResources().getColor(R.color.option_message_background_color));
        mMessage.setTextColor(getResources().getColor(R.color.option_message_text_color));
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addView(mMessage, 0);
        addView(mRecyclerView, 1);
    }

    public MultiOptionView setDataCollecter(MultiOptionDataCollecter collecter) {
        mMessage.setText(collecter.getData().getMessage());
        ArrayList<Map.Entry<String, ApplicationResponse.OnClickListener>> arrayList = new ArrayList<>(collecter.getData().getActions().entrySet());
        mRecyclerView.setAdapter(new OptionAdapter(collecter, arrayList));
        return this;
    }

    /**
     * Clase Adapter que nos ayuda a mostrar cada una de las opciones de las instancias de la clase ApplicationResponse
     * @see ApplicationResponse
     */
    protected static class OptionAdapter extends RecyclerView.Adapter<OptionHolder> {

        private ArrayList<Map.Entry<String, ApplicationResponse.OnClickListener>> mArrayList;
        private MultiOptionDataCollecter mDialogCloseListener;

        public OptionAdapter(MultiOptionDataCollecter dialogCloseListener, ArrayList<Map.Entry<String, ApplicationResponse.OnClickListener>> arrayList) {
            mDialogCloseListener = dialogCloseListener;
            mArrayList = arrayList;
        }

        /**
         * Llamado por el adapter cuando se necesita crear un contenedor
         * @param parent padre de la vista
         * @param viewType tipo de vista
         * @return instancia del contenedor
         */
        @Override
        public OptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new OptionHolder(mDialogCloseListener, linearLayout);
        }

        /**
         * Llamado por el adapter cuando va a visualizar datos en el contenedor
         * @param holder contenedor
         * @param position posición del contenedor
         */
        @Override
        public void onBindViewHolder(OptionHolder holder, int position) {
            holder.bindViewHolder(mArrayList.get(position).getKey(), mArrayList.get(position).getValue());
        }

        /**
         * Llamado por el adapter cuando necesita saber cuántos elementos tendrá la lista
         * @return numero de elementos
         */
        @Override
        public int getItemCount() {
            return mArrayList.size();
        }
    }

    /**
     * Clase ViewHolder que sirve como contenedor de un elemento de la lista RecyclerView
     */
    private static class OptionHolder extends RecyclerView.ViewHolder implements OnClickListener {

        private TextView mMessage;
        private ApplicationResponse.OnClickListener mClickListener;
        private MultiOptionDataCollecter mDialogCloseListener;

        public OptionHolder(MultiOptionDataCollecter dialogCloseListener, LinearLayout itemView) {
            super(itemView);
            mDialogCloseListener = dialogCloseListener;
            int paddingDp = SizeUtils.paddingDpTransform(itemView.getContext(), 16);
            mMessage = new TextView(itemView.getContext());
            mMessage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mMessage.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            mMessage.setTextColor(itemView.getContext().getResources().getColor(R.color.option_text_color));
            mMessage.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.option_background_color));
            itemView.addView(mMessage);
            itemView.setOnClickListener(this);
        }

        /**
         * Muestra el texto enviado como parámetro en el contenedor y agrega la acción del text
         * @param text texto
         * @param clickListener acción
         */
        public void bindViewHolder(String text, ApplicationResponse.OnClickListener clickListener) {
            mMessage.setText(text);
            mClickListener = clickListener;
        }

        /**
         * Llamado por el sistema android cuando alguien presione un contenedor que tenga el onclick listener
         * @param view contenedor presionado
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onClick(mDialogCloseListener.getData(), itemView);
            }
            mDialogCloseListener.close();
        }
    }

    /**
     * Listener de cerrado, utilizado por la un adapter para llamar a la función close de diferentes diálogos o modales
     *
     * Created by Nicol Acosta on 10/19/16.
     * nicol@parkiller.com
     */
    public interface MultiOptionDataCollecter {
        void close();
        ApplicationResponse getData();
    }

}
