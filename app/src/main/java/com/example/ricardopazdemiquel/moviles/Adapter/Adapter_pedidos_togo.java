package com.example.ricardopazdemiquel.moviles.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.Detalle_viaje_Cliente;
import com.example.ricardopazdemiquel.moviles.Dialog.Producto_togo_Dialog;
import com.example.ricardopazdemiquel.moviles.PedirSieteTogo;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Adapter_pedidos_togo extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    MenuItem item;

    public Adapter_pedidos_togo(Context contexto, JSONArray array) {
        this.contexto = contexto;
        this.array = array;
    }

    public JSONArray getArray(){
        return array;
    }
    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public JSONObject getItem(int i) {
        try {
            return array.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_pedidos_togo , viewGroup, false);
        }

        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( MotionEvent.ACTION_DOWN);
                view.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return false;
            }
        });*/

        TextView text_producto = view.findViewById(R.id.text_producto);
        TextView text_cantidad = view.findViewById(R.id.text_cantidad);
        ImageView Editar = view.findViewById(R.id.editar);
        ImageView Elminar = view.findViewById(R.id.eliminar);
        try {
            JSONObject obj =  array.getJSONObject(i);
            text_producto.setText(obj.getString("producto"));
            text_cantidad.setText(obj.getString("cantidad"));
            view.setTag(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Elminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.eliminar:
                        ((PedirSieteTogo) contexto).removeItem(i);
                        break;
                    case R.id.action_update_producto:

                        break;
                }
            }

        });

        return view;

    }

    public void addItem(JSONObject obj){
        if(array!=null){
            array.put(obj);
        }
    }

    public void removeiten(int pos){
        if(array!=null){
            array.remove(pos);
        }
    }

    public void updateItem(JSONObject obj ,int pos){
        if(array!=null){
            try {
                array.put(pos,obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
