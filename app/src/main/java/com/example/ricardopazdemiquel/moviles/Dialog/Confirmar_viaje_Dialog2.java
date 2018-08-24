package com.example.ricardopazdemiquel.moviles.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.ricardopazdemiquel.moviles.R;

/**
 * Created by Edson on 02/12/2017.
 */

public class Confirmar_viaje_Dialog2 extends DialogFragment implements View.OnClickListener {

    private Button btn_aceptar;

    public static String APP_TAG = "registro";

    private static final String TAG = Confirmar_viaje_Dialog2.class.getSimpleName();

    public Confirmar_viaje_Dialog2() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog2_confirma_viaje, null);
        builder.setView(v);

        btn_aceptar = v.findViewById(R.id.btn_aceptarD);
        btn_aceptar.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_aceptarD:
                dismiss();
                break;
        }
    }

 }


