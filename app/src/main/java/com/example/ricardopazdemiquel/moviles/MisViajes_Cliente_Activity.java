package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class MisViajes_Cliente_Activity extends AppCompatActivity {

    private static final String TAG ="fragment_explorar";
    private ListView lv;

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_list_mis_viajes);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = findViewById(R.id.list_misViajes);

        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new User_getPerfil(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Opcion para ir atras sin reiniciar el la actividad anterior de nuevo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
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

    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(MisViajes_Cliente_Activity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_mis_viajes");
            parametros.put("id",id);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            progreso.dismiss();
            if (success != null || !success.isEmpty()){
                try {
                    JSONArray jsonArray = new JSONArray(success);
                    Adaptador_mis_viajes mis_viajes= new Adaptador_mis_viajes(MisViajes_Cliente_Activity.this,jsonArray);
                    lv.setAdapter(mis_viajes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                return;
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
