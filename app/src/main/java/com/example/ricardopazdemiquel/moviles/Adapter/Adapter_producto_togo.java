package com.example.ricardopazdemiquel.moviles.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.Detalle_viaje_Cliente;
import com.example.ricardopazdemiquel.moviles.Model.Producto_togo;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Adapter_producto_togo extends BaseAdapter {

    private List<Producto_togo> items;
    private Context contexto;
    private static final int EFECTIVO = 1;
    private static final int CREDITO = 2;

    public Adapter_producto_togo(Context contexto, List<Producto_togo> list) {
        this.contexto = contexto;
        this.items = list;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_ultimos_viajes, viewGroup, false);
        }
        Producto_togo item = items.get(i);
        TextView text_ = view.findViewById(R.id.text_fecha);
        TextView text_auto = view.findViewById(R.id.text_auto);
        TextView text_inicio = view.findViewById(R.id.text_inicioViaje);

        return view;
    }
}
