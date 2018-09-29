package com.example.ricardopazdemiquel.moviles.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_favoritos;
import com.example.ricardopazdemiquel.moviles.PedirSieteMap;
import com.example.ricardopazdemiquel.moviles.R;
import com.example.ricardopazdemiquel.moviles.favoritos_pruba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class List_favoritos_fragment extends Fragment implements View.OnClickListener {

    private static final String TAG ="fragment_explorar";
    private JSONObject carrera;
    private Button btn_agregar_favoritos;
    private ListView lista_favoritos;
    private Adapter_favoritos adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        btn_agregar_favoritos = view.findViewById(R.id.btn_agregar_favoritos);
        lista_favoritos = view.findViewById(R.id.lista_favoritos);

        btn_agregar_favoritos.setOnClickListener(this);

        lista_favoritos.setOnTouchListener(new View.OnTouchListener() {
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

        lista_favoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    JSONObject obj = new JSONObject(view.getTag().toString());
                    String nombre = obj.getString("nombre_favorito");
                    Double latFin = obj.getDouble("latFin");
                    Double lngFin = obj.getDouble("lngFin");
                    ((PedirSieteMap)getActivity()).Verificar_tipo_siete(10,nombre , latFin, lngFin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargar();
    }



    private void cargar(){
        //carga un SharedPreferences de favoritos o crea uno vacio
        JSONArray productos = get_list_Favoritos();
        if(productos==null)
        {
            SharedPreferences preferencias = getActivity().getSharedPreferences("myPref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            productos=new JSONArray();
            editor.putString("lista_favoritos", productos.toString());
            editor.commit();
        }
        adapter = new Adapter_favoritos(getActivity(),productos);
        lista_favoritos.setAdapter(adapter);
        JSONObject obj = new JSONObject();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_agregar_favoritos:
                Intent  intent = new Intent(getActivity() ,favoritos_pruba.class);
                startActivity(intent);
                break;
        }
    }

}
