package com.example.ricardopazdemiquel.moviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_favoritos;
import com.example.ricardopazdemiquel.moviles.Fragment.List_historial_fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Favoritos_Clientes extends AppCompatActivity implements View.OnClickListener {

    private Button btn_elegir_destino;
    private LinearLayout container_frame;
    private ListView lv_List_favoritos;
    private Adapter_favoritos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_clientes);

        container_frame = findViewById(R.id.container_frame);
        btn_elegir_destino = findViewById(R.id.btn_elegir_destino);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        lv_List_favoritos = findViewById(R.id.lv_List_favoritos);

        cargar();

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void cargar(){
        //carga un SharedPreferences de favoritos o crea uno vacio
        JSONArray productos = get_list_Favoritos();
        if(productos==null)
        {
            SharedPreferences preferencias = Favoritos_Clientes.this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            productos=new JSONArray();
            editor.putString("lista_favoritos", productos.toString());
            editor.commit();
        }
        adapter = new Adapter_favoritos(Favoritos_Clientes.this,productos);
        lv_List_favoritos.setAdapter(adapter);
        JSONObject obj = new JSONObject();
    }

    public JSONArray get_list_Favoritos() {
        SharedPreferences preferencias = Favoritos_Clientes.this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_elegir_destino:
                container_frame.setVisibility(View.GONE);
                break;
        }
    }

}
