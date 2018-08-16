package com.example.ricardopazdemiquel.moviles.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.R;
import com.example.ricardopazdemiquel.moviles.finalizar_viajeCliente;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class List_historial_fragment extends Fragment {

    private static final String TAG ="fragment_explorar";
    private JSONObject carrera;

    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        lv = view.findViewById(R.id.lista_historial);

        carrera=((finalizar_viajeCliente)getActivity()).get_carrera();
        cargar();
        return view;
    }


    private void cargar(){
        try {
            String nombre  = carrera.getString("nombre");
            String apellidoP  = carrera.getString("apellido_pa");
            String apellidoM  = carrera.getString("apellido_ma");
            String placa  = carrera.getString("placa");
            String telefono  = carrera.getString("telefono");
            double lat_i= carrera.getDouble("latinicial");
            double lat_f  = carrera.getDouble("latfinal");
            double lng_i = carrera.getDouble("lnginicial");
            double lng_f = carrera.getDouble("lngfinal");
            String inicial = get_localizacion(lat_i , lng_i);
            String finales =  get_localizacion(lat_f , lng_f);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String get_localizacion(double LATITUDE, double LONGITUDE) {
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
