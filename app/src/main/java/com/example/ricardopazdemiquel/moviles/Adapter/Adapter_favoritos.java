package com.example.ricardopazdemiquel.moviles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Adapter_favoritos extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    MenuItem item;

    public Adapter_favoritos(Context contexto, JSONArray array) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_favoritos , viewGroup, false);
        }

        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( MotionEvent.ACTION_DOWN);
                view.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return false;
            }
        });*/

        TextView text_nombre = view.findViewById(R.id.text_nombre);
        TextView text_ubicacion = view.findViewById(R.id.text_ubicacion);
        try {
            JSONObject obj =  array.getJSONObject(i);
            text_nombre.setText(obj.getString("nombre"));
            text_ubicacion.setText(obj.getString("ubicacion"));
            view.setTag(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }


}
