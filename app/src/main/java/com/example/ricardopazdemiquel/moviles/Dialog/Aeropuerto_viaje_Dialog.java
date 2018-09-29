package com.example.ricardopazdemiquel.moviles.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.ricardopazdemiquel.moviles.Calcular_ruta_activity;
import com.example.ricardopazdemiquel.moviles.PedirSieteTogo;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONException;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Aeropuerto_viaje_Dialog extends DialogFragment implements View.OnClickListener {

    private Button btn_cancelar;
    private Button btn_confirmar;
    private int tipo;

    private static final int TIPO_TOGO = 2;
    public static String APP_TAG = "registro";

    private static final String TAG = Aeropuerto_viaje_Dialog.class.getSimpleName();

    @SuppressLint("ValidFragment")
    public Aeropuerto_viaje_Dialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirma_aeropuerto, null);
        builder.setView(v);

        btn_cancelar = v.findViewById(R.id.btn_cancelarD);
        btn_confirmar = v.findViewById(R.id.btn_confirmarD);

        btn_cancelar.setOnClickListener(this);
        btn_confirmar.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirmarD:
               dismiss();
                break;
            case R.id.btn_cancelarD:
                getActivity().finish();
                break;
        }
    }

 }


