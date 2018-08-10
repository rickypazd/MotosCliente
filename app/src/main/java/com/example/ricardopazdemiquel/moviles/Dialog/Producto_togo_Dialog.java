package com.example.ricardopazdemiquel.moviles.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_producto_togo;
import com.example.ricardopazdemiquel.moviles.EsperandoConductor;
import com.example.ricardopazdemiquel.moviles.Model.Producto_togo;
import com.example.ricardopazdemiquel.moviles.PedirSieteMap;
import com.example.ricardopazdemiquel.moviles.Producto_togo_Activity;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Producto_togo_Dialog extends DialogFragment implements View.OnClickListener {

    private ImageView btn_cancelar;
    private Button btn_confirmar_togo;
    private Button btn_editar_togo;
    private TextView text_pedido;
    private TextView text_descripcion;
    private TextView text_cantidad;

    public static String APP_TAG = "registro";

    private static final String TAG = Producto_togo_Dialog.class.getSimpleName();
    private JSONObject obj;
    private int pos;
    private int tipo;
    private static final int EDITAR = 1;
    private static final int AGREGAR = 2;

    @SuppressLint("ValidFragment")
    public Producto_togo_Dialog(JSONObject json , int pos , int tipo ) {
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
        View v = inflater.inflate(R.layout.dialog_producto_togo, null);
        builder.setView(v);


        btn_confirmar_togo = v.findViewById(R.id.btn_confirmar_togo);
        btn_editar_togo = v.findViewById(R.id.btn_editar_togo);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        text_pedido = v.findViewById(R.id.text_pedido);
        text_descripcion = v.findViewById(R.id.text_descripcion);
        text_cantidad = v.findViewById(R.id.text_cantidad);

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
            text_cantidad.setText(obj.getString("cantidad"));
            text_descripcion.setText(obj.getString("descripcion"));
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
            case R.id.btn_editar_togo:
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
        String descricipn = text_descripcion.getText().toString().trim();
        String cantidad = text_cantidad.getText().toString().trim();
        if(pedido.isEmpty()){
            text_pedido.setError("campo obligatorio");
            acept = false;
        }
        if(cantidad.isEmpty()){
            text_cantidad.setError("campo obligatorio");
            acept = false;
        }
        if(!acept){
            return;
        }
        Producto_togo producto_togo = new Producto_togo();

        JSONObject object =  new JSONObject();
        try {
            object.put("producto" ,pedido);
            object.put("descripcion" ,descricipn);
            object.put("cantidad" ,cantidad);
            if(tipo == AGREGAR){
                ((Producto_togo_Activity)getActivity()).InsertList(object);
                dismiss();
            }else if(tipo == EDITAR){
                ((Producto_togo_Activity)getActivity()).UpdateList(object,pos);
                dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

 }


