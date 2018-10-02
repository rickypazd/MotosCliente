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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class FinalizarViajeFragment_2 extends Fragment implements View.OnClickListener {

    private static final String TAG = "fragment_explorar";
    private EditText edit_mensaje;
    private Button btn_enviar_mensaje;
    /*private ImageView btn_amable;
    private ImageView btn_buena_ruta;
    private ImageView btn_auto_limpio;*/
    boolean amable = false;
    boolean buenaRuta = false;
    boolean autoLimpio= false;

    public FinalizarViajeFragment_2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finalizar_viaje_fragment_2, container, false);

        edit_mensaje = view.findViewById(R.id.edit_mensaje);
        /*btn_amable = view.findViewById(R.id.btn_amable);
        btn_buena_ruta = view.findViewById(R.id.btn_buena_ruta);
        btn_auto_limpio = view.findViewById(R.id.btn_auto_limpio);*/
        btn_enviar_mensaje = view.findViewById(R.id.btn_enviar_mensaje);

        btn_enviar_mensaje.setOnClickListener(this);
        /*btn_amable.setOnClickListener(this);
        btn_buena_ruta.setOnClickListener(this);
        btn_auto_limpio.setOnClickListener(this);*/

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enviar_mensaje:
                String mensaje = edit_mensaje.getText().toString().trim();
                ((finalizar_viajeCliente)getActivity()).finalizo(mensaje , amable , autoLimpio, buenaRuta);

                break;
            /*case R.id.btn_amable:
                if(amable == false){
                    view.setAlpha(0.5f);
                    amable = true;
                }else{
                    view.setAlpha(1f);
                    amable = false;
                }
                break;
            case R.id.btn_buena_ruta:
                if(buenaRuta == false){
                    view.setAlpha(0.5f);
                    buenaRuta = true;
                }else{
                    view.setAlpha(1f);
                    buenaRuta = false;
                }
                break;
            case R.id.btn_auto_limpio:
                if(autoLimpio == false){
                    view.setAlpha(0.5f);
                    autoLimpio = true;
                }else{
                    view.setAlpha(1f);
                    autoLimpio = false;
                }
                break;*/
        }
    }
}
