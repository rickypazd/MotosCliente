package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_producto_togo;
import com.example.ricardopazdemiquel.moviles.Dialog.Cancelar_viaje_Dialog;
import com.example.ricardopazdemiquel.moviles.Dialog.Producto_togo_Dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class Producto_togo_Activity extends AppCompatActivity implements View.OnClickListener ,AdapterView.OnItemClickListener{

    private static final String TAG ="fragment_explorar";
    private ListView lv;
    private Adapter_producto_togo adapter;



    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_list_producto_togo);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = findViewById(R.id.list_producto_togo);
        lv.setOnItemClickListener(this);
        //crea
        JSONArray prosductos=getProductosPendientes();
        if(prosductos==null)
        {
            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            prosductos=new JSONArray();
            editor.putString("productos_pendientes", prosductos.toString());
            editor.commit();
        }

        adapter = new Adapter_producto_togo(Producto_togo_Activity.this,prosductos);
        lv.setAdapter(adapter);
        JSONObject obj = new JSONObject();
        /*try {
            obj.put("producto","cfsdf");
            obj.put("cantidad","sfsdf");
            obj.put("descripcion","gsvvdv");
            adapter.addItem(obj);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


    }

    public JSONArray getProductosPendientes() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String productos = preferencias.getString("productos_pendientes", "");
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.list_producto_togo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_producto, menu);
        }
    }

    String nombre;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(info == null || info.position < 0) {
            return true;
        }
        View view = info.targetView;
        int pos = info.position;
        JSONObject obj = null;

        try {
            obj = new JSONObject(view.getTag().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (item.getItemId()) {
            case R.id.action_delate_producto:
                removeItem(pos);
                break;
            case R.id.action_update_producto:
                android.app.FragmentManager fragmentManager = getFragmentManager();
                new Producto_togo_Dialog(obj,pos,1).show(fragmentManager, "Dialog");
                break;
        }
        return true;
    }

    public void InsertList(JSONObject object){
        adapter.addItem(object);
        adapter.notifyDataSetChanged();
        JSONArray arr = adapter.getArray();
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("productos_pendientes", arr.toString());
        editor.commit();
    }

    public void UpdateList(JSONObject object , int pos){
        adapter.updateItem(object,pos);
        adapter.notifyDataSetChanged();
        JSONArray arr = adapter.getArray();
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("productos_pendientes", arr.toString());
        editor.commit();
    }

    public void removeItem(int pos){
        adapter.removeiten(pos);
        adapter.notifyDataSetChanged();
        JSONArray arr = adapter.getArray();
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("productos_pendientes", arr.toString());
        editor.commit();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                android.app.FragmentManager fragmentManager = getFragmentManager();
                new Producto_togo_Dialog(new JSONObject() , 0 , 2).show(fragmentManager, "Dialog");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        registerForContextMenu(lv);
    }
}
