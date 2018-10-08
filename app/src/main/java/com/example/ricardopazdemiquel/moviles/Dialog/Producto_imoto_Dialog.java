package com.example.ricardopazdemiquel.moviles.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.Calcular_ruta_activity;
import com.example.ricardopazdemiquel.moviles.PedirSieteTogo;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Producto_imoto_Dialog extends DialogFragment implements View.OnClickListener {

    private ImageView btn_cancelar;
    private Button btn_confirmar_togo;
    private Button btn_editar_togo;
    private TextView text_pedido;

    public static String APP_TAG = "registro";

    private static final String TAG = Producto_imoto_Dialog.class.getSimpleName();
    private JSONObject obj;
    private int pos;
    private int tipo;
    private static final int EDITAR = 1;
    private static final int AGREGAR = 2;

    @SuppressLint("ValidFragment")
    public Producto_imoto_Dialog(JSONObject json , int pos , int tipo) {
        this.obj = json;
        this.pos = pos;
        this.tipo = tipo;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_producto_imoto, null);
        builder.setView(v);

        btn_confirmar_togo = v.findViewById(R.id.btn_confirmar_togo);
        btn_editar_togo = v.findViewById(R.id.btn_editar_togo);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        text_pedido = v.findViewById(R.id.text_pedido);

        btn_cancelar.setOnClickListener(this);
        btn_confirmar_togo.setOnClickListener(this);
        btn_editar_togo.setOnClickListener(this);

        switch (tipo){
            case EDITAR:
                cargar(obj);
                break;
            case AGREGAR:
                break;
        }
        return builder.create();
    }

    public  void cargar(JSONObject obj){
        try {
            btn_confirmar_togo.setVisibility(View.GONE);
            btn_editar_togo.setVisibility(View.VISIBLE);
            text_pedido.setText(obj.getString("producto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirmar_togo:
                agregar_pedido(tipo);
                break;
            case R.id.btn_cancelar:
                dismiss();
                break;
        }
    }
    private void agregar_pedido(int tipo) {
        boolean acept = true;
        String pedido = text_pedido.getText().toString().trim();
        if(pedido.isEmpty()){
            text_pedido.setError("campo obligatorio");
            acept = false;
        }
        if(!acept){
            return;
        }
        //JSONObject object =  new JSONObject();
        try {
            obj.put("mensaje" ,pedido);
            if(tipo == AGREGAR){
                //((PedirSieteTogo)getActivity()).InsertList(object);
                Intent intent = new Intent(getActivity(), Calcular_ruta_activity.class);
                intent.putExtra("JSON", String.valueOf(obj));
                startActivity(intent);
                dismiss();
            }else if(tipo == EDITAR){
           //     ((PedirSieteTogo)getActivity()).UpdateList(object,pos);
           //     dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

 }


