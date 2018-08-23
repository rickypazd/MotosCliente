package com.example.ricardopazdemiquel.moviles.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_favoritos;
import com.example.ricardopazdemiquel.moviles.Detalle_viaje_Cliente;
import com.example.ricardopazdemiquel.moviles.EsperandoConductor;
import com.example.ricardopazdemiquel.moviles.Fragment.List_favoritos_fragment;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Add_ubicacion_favoritos_Dialog extends DialogFragment implements View.OnClickListener {

    private ImageView btn_cancelar;
    private Button btn_agregar_favorito;
    private TextView text_direccion;
    private EditText edit_nombre_ubicacion;

    public static String APP_TAG = "registro";
    private Adapter_favoritos adapter;

    private static final String TAG = Add_ubicacion_favoritos_Dialog.class.getSimpleName();
    private JSONObject obj;
    JSONArray array = new JSONArray();

    @SuppressLint("ValidFragment")
    public Add_ubicacion_favoritos_Dialog(JSONObject json_agregar_favoritos) {
        this.obj = json_agregar_favoritos;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add_favorito, null);
        builder.setView(v);

        btn_agregar_favorito = v.findViewById(R.id.btn_agregar_favorito);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        edit_nombre_ubicacion = v.findViewById(R.id.edit_nombre_ubicacion);
        text_direccion = v.findViewById(R.id.text_direccion);
        btn_cancelar.setOnClickListener(this);
        btn_agregar_favorito.setOnClickListener(this);

        try {
            Double latFin = obj.getDouble("latFin");
            Double lngFin = obj.getDouble("lngFin");
            String ubicacion = getCompleteAddressString(latFin,lngFin);
            text_direccion.setText(ubicacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        try {
            obj.put("nombre_favorito", nombre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InsertList(obj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_agregar_favorito:
                agregar_ubicacion();
                dismiss();
                break;
            case R.id.btn_cancelar:
                dismiss();
                break;
        }
    }

    public void InsertList(JSONObject object){
       // adapter.addItem(object);
      //  adapter.notifyDataSetChanged();
       // JSONArray arr = adapter.getArray();
        JSONArray arr=get_list_Favoritos();
        if(arr==null){
            arr=new JSONArray();
        }
        arr.put(obj);
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("lista_favoritos", arr.toString());
        editor.commit();
    }

    public JSONArray get_list_Favoritos() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String productos = preferencias.getString("lista_favoritos", "");
        if (productos.length() <= 0) {
            return null;
        } else {
            try {
                JSONArray productosObj = new JSONArray(productos);
                return productosObj;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public void UpdateList(JSONObject object , int pos){
        adapter.updateItem(object,pos);
        adapter.notifyDataSetChanged();
        JSONArray arr = adapter.getArray();
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("lista_favoritos", arr.toString());
        editor.commit();
    }

    public void removeItem(int pos){
        adapter.removeiten(pos);
        adapter.notifyDataSetChanged();
        JSONArray arr = adapter.getArray();
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("lista_favoritos", arr.toString());
        editor.commit();
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction addr", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction addr", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction addr", "Canont get Address!");
        }
        return strAdd;
    }

 }


