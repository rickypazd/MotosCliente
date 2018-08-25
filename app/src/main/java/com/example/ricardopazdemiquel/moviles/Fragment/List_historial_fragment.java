package com.example.ricardopazdemiquel.moviles.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_favoritos;
import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_historial;
import com.example.ricardopazdemiquel.moviles.MainActivity;
import com.example.ricardopazdemiquel.moviles.PedirSieteMap;
import com.example.ricardopazdemiquel.moviles.R;
import com.example.ricardopazdemiquel.moviles.finalizar_viajeCliente;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class List_historial_fragment extends Fragment {

    private static final String TAG ="fragment_explorar";
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        lv = view.findViewById(R.id.lista_historial);

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    ((PedirSieteMap)getActivity()).close();
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    ((PedirSieteMap)getActivity()).open();
                }else if(event.getAction()==MotionEvent.ACTION_CANCEL){
                    ((PedirSieteMap)getActivity()).open();
                }
                return false;
            }
        });

        new get_historial().execute();

        return view;
    }

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONObject usr_log = new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class get_historial extends AsyncTask<Void, String, String> {

        String id_usr;
        {
            try {
                id_usr = getUsr_log().getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(getActivity());
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_historial_ubic");
            param.put("id", id_usr);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            } catch (Exception e) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp.isEmpty() || resp == null){
                Toast.makeText(getActivity(),"Error al obtener Datos", Toast.LENGTH_SHORT).show();
            }else  {
                try {
                    JSONArray array  = new JSONArray(resp);
                    Adapter_historial adapter = new Adapter_historial(getActivity(),array);
                    lv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }
    }

}
