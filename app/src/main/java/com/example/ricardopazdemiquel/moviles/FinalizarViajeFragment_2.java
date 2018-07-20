package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class FinalizarViajeFragment_2 extends Fragment implements View.OnClickListener{

    private static final String TAG ="fragment_explorar";
    private EditText edit_mensaje;

    public FinalizarViajeFragment_2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finalizar_viaje_fragment_2, container, false);

        edit_mensaje = view.findViewById(R.id.edit_mensaje);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enviar_mensaje:
                String mensaje=edit_mensaje.getText().toString();
                ((finalizar_viajeCliente)getActivity()).finalizo(mensaje);
                break;
        }
    }




}
