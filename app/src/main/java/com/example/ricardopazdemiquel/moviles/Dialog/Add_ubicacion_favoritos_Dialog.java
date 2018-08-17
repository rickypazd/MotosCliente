package com.example.ricardopazdemiquel.moviles.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.EsperandoConductor;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Add_ubicacion_favoritos_Dialog extends DialogFragment implements View.OnClickListener {

    private ImageView btn_cancelar;
    private Button btn_agregar_ubicacion;
    private TextView text_direccion;
    private EditText edit_nombre_ubicacion;

    public static String APP_TAG = "registro";

    private static final String TAG = Add_ubicacion_favoritos_Dialog.class.getSimpleName();
    private JSONObject obj;

    @SuppressLint("ValidFragment")
    public Add_ubicacion_favoritos_Dialog(JSONObject json_cancelarViaje) {
        this.obj=json_cancelarViaje;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_cancelar_viaje, null);
        builder.setView(v);

        btn_agregar_ubicacion = v.findViewById(R.id.btn_confirmar_cancelacion);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        edit_nombre_ubicacion = v.findViewById(R.id.edit_nombre_ubicacion);
        text_direccion = v.findViewById(R.id.text_direccion);

        btn_cancelar.setOnClickListener(this);
        btn_agregar_ubicacion.setOnClickListener(this);

        return builder.create();
    }

    private void agregar_ubicacion(){
        boolean acept = true;
        String nombre = edit_nombre_ubicacion.getText().toString().trim();
        if(nombre.isEmpty()){
            acept = false;
        }
        if(!acept){
            return;
        }
        JSONObject obj=new JSONObject();
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("lista_favoritos","");
        editor.commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_agregar_ubicacion:
                agregar_ubicacion();
                    dismiss();
                break;
            case R.id.btn_cancelar:
                dismiss();
                break;
        }
    }

 }


