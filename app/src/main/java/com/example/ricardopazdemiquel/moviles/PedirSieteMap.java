package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.DirectionsJSONParser;
import utiles.Token;

public class PedirSieteMap extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    MapView mMapView;
    private GoogleMap googleMap;
    private boolean entroLocation=false;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoCompleteTextView mAutocompleteTextView2;
    private AutoCompleteTextView selected;
    private TextView monto;
    private Button btn_confirmar;
    private ImageView iv_marker;
    private LinearLayout ll_ubic;
    private LinearLayout linear_confirm;
    private LinearLayout linearLayoutPedir;
    private LinearLayout linearLayoutTogo;
    private ConstraintLayout layoutButon;
    private ConstraintLayout btn_estandar_recicler;
    private LatLng inicio;
    private LatLng fin;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private RecyclerView recyclerView;
    private int tipo_pago;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.9720));

    JSONObject usr_log;
    //inicializamos los botones para pedir siete y el tipo de carrera
    private Button btn_pedir_super , btn_pedir_maravilla ,btn_pedir_togo , btn_pedir_estandar;
    private int tipo_carrera;

    // inicializamos los iconos de confirmar carrera
    private TextView icono1, icono2 ,icono3 , icono4 ,icono5, icono6,icono7;
    double mont;
    private AutoCompleteTextView text_direccion_togo;
    private Button btn_agregar_producto;

    public PedirSieteMap() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_siete_map);

        ll_ubic=findViewById(R.id.linearLayoutPedir);
        linearLayoutPedir = findViewById(R.id.linearLayoutPedir);
        linearLayoutTogo = findViewById(R.id.linearLayoutTogo);
        layoutButon=findViewById(R.id.ll_boton);
        iv_marker=findViewById(R.id.ivmarker);
        monto = findViewById(R.id.tv_monto);
        text_direccion_togo = findViewById(R.id.text_direccion_togo);
        btn_agregar_producto = findViewById(R.id.btn_agregar_producto);
        btn_agregar_producto.setOnClickListener(this);

        final double longitudeGPS=getIntent().getDoubleExtra("lng",0);
        final double latitudeGPS=getIntent().getDoubleExtra("lat",0);

        tipo_carrera = getIntent().getIntExtra("tipo",0);


        mGoogleApiClient = new GoogleApiClient.Builder(PedirSieteMap.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        btn_pedir_super = findViewById(R.id.btn_pedir_super);
        btn_pedir_maravilla = findViewById(R.id.btn_pedir_maravilla);
        btn_pedir_togo = findViewById(R.id.btn_pedir_togo);
        btn_pedir_estandar = findViewById(R.id.btn_pedir_estandar);
        recyclerView = findViewById(R.id.reciclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mylinear = new LinearLayoutManager(this);
        mylinear.setOrientation(LinearLayoutManager.HORIZONTAL);
        try {
            JSONArray arr = new JSONArray();
            JSONObject obj1= new JSONObject();
            obj1.put("id",1);
            obj1.put("nombre","Estandar");
            arr.put(obj1);
            JSONObject obj2= new JSONObject();
            obj2.put("id",5);
            obj2.put("nombre","4X4");
            arr.put(obj2);
            JSONObject obj3= new JSONObject();
            obj3.put("id",6);
            obj3.put("nombre","Camioneta");
            arr.put(obj3);
            JSONObject obj4= new JSONObject();
            obj4.put("id",7);
            obj4.put("nombre","6 pasajeros");
            arr.put(obj4);
            AdaptadorSieteEstandar ada = new AdaptadorSieteEstandar(arr,this,PedirSieteMap.this);
           recyclerView.setAdapter(ada);
            recyclerView.setLayoutManager(mylinear);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RadioButton radio_efectivo = findViewById(R.id.radio_efectivo);
        final RadioButton radio_credito = findViewById(R.id.radio_credito);
        radio_efectivo.setOnClickListener(this);
        radio_credito.setOnClickListener(this);

        icono1 = findViewById(R.id.icono1);
        icono2 = findViewById(R.id.icono2);
        icono3 = findViewById(R.id.icono3);
        icono4 = findViewById(R.id.icono4);
        icono5 = findViewById(R.id.icono5);
        icono6 = findViewById(R.id.icono6);
        icono7 = findViewById(R.id.icono7);

        btn_pedir_estandar.setOnClickListener(this);
        btn_pedir_super.setOnClickListener(this);
        btn_pedir_maravilla.setOnClickListener(this);
        btn_pedir_togo.setOnClickListener(this);

        linear_confirm=findViewById(R.id.linear_confirm);

        mostar_button(tipo_carrera);

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setOnFocusChangeListener(this);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        mAutocompleteTextView2 = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView2);
        mAutocompleteTextView2.setOnFocusChangeListener(this);
        mAutocompleteTextView2.setThreshold(3);
        mAutocompleteTextView2.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView2.setAdapter(mPlaceArrayAdapter);

        text_direccion_togo = (AutoCompleteTextView) findViewById(R.id
                .text_direccion_togo);
        text_direccion_togo.setOnFocusChangeListener(this);
        text_direccion_togo.setThreshold(3);
        text_direccion_togo.setOnItemClickListener(mAutocompleteClickListener);
        text_direccion_togo.setAdapter(mPlaceArrayAdapter);

        usr_log = getUsr_log();

        if (usr_log == null) {
            Intent intent = new Intent(PedirSieteMap.this, LoginCliente.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        btn_confirmar= findViewById(R.id.btn_confirmar);
        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String id = usr_log.getString("id");
                    String resp =new User_getPerfil(id).execute().get();
                    if(resp==null){
                        Toast.makeText(PedirSieteMap.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
                    }else{
                        android.app.FragmentManager fragmentManager = getFragmentManager();
                        if (!resp.isEmpty()){
                            JSONObject usr = new JSONObject(resp);
                            if(usr.getString("exito").equals("si")){
                                double credito = usr.getDouble("creditos");
                                boolean acept = true;
                                if(radio_credito.isChecked() == true){
                                    tipo_pago=2;
                                    if(credito < mont){
                                        new Confirmar_viaje_Dialog2().show(fragmentManager, "Dialog");
                                        acept=false;
                                    }
                                }
                                else {
                                    tipo_pago=1;
                                    if(credito < 0){
                                        new Confirmar_viaje_Dialog().show(fragmentManager, "Dialog");
                                        //esta en deuda , aler se cobrara el monto + viej
                                        acept=false;
                                    }
                                }
                                if(acept){
                                    ok_predir_viaje();
                                }

                            }
                        }

                    }


                } catch (JSONException e) {
                        e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });


        mMapView = findViewById(R.id.mapviewPedirSiete);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeGPS, longitudeGPS), 14);
                googleMap.animateCamera(cu);
                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(PedirSieteMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PedirSieteMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if (!entroLocation){
                            entroLocation=true;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14);
                            googleMap.animateCamera(cu);
                        }
                    }
                });

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if(selected!=null){
                            LatLng center=googleMap.getCameraPosition().target;
                            selected.setText(getCompleteAddressString(center.latitude,center.longitude));
                            selected.setTag(center);
                        }

                    }
                });

            }
        });

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            ImageView locationButton = (ImageView) ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 600);
            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);

        }
    }

    public void ok_predir_viaje() throws JSONException {
        Intent inte = new Intent(PedirSieteMap.this, PidiendoSiete.class);
        inte.putExtra("latInicio", inicio.latitude + "");
        inte.putExtra("lngInicio", inicio.longitude + "");
        inte.putExtra("latFin", fin.latitude + "");
        inte.putExtra("lngFin", fin.longitude + "");
        inte.putExtra("token", Token.currentToken);
        inte.putExtra("id_usr", usr_log.getInt("id") + "");
        inte.putExtra("tipo", tipo_carrera + "");
        inte.putExtra("tipo_pago", tipo_pago + "");
        startActivity(inte);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pedir_estandar:
                calculando_ruta(view , tipo_carrera);
                break;
            case R.id.btn_pedir_super:
                calculando_ruta(view , tipo_carrera);
                break;
            case R.id.btn_pedir_maravilla:
                calculando_ruta(view , tipo_carrera);
                break;
            case R.id.btn_pedir_togo:
                calculando_ruta(view , tipo_carrera);
                break;
            case R.id.btn_agregar_producto:
                Intent intent =  new Intent(PedirSieteMap.this, Producto_togo_Activity.class);
                startActivity(intent);
                break;
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18);
            googleMap.animateCamera(cu);

        }
    };

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            selected=(AutoCompleteTextView) v;

        }
    }

    private String obtenerDireccionesURL(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String key = "key="+getString(R.string.apikey);

        String parameters = str_origin+"&"+str_dest;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&"+key;

        return url;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("ERROR AL OBTENER INFO D",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.rgb(0,0,255));
            }
            if(lineOptions!=null) {
                googleMap.addPolyline(lineOptions);

                int size = points.size() - 1;
                float[] results = new float[1];
                float sum = 0;

                for(int i = 0; i < size; i++){
                    Location.distanceBetween(
                            points.get(i).latitude,
                            points.get(i).longitude,
                            points.get(i+1).latitude,
                            points.get(i+1).longitude,
                            results);
                    sum += results[0];
                }

                try {
                    String resp = new validar_precio(tipo_carrera).execute().get();
                    if(resp==null){
                        Toast.makeText(PedirSieteMap.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
                    }else{
                        JSONObject object = new JSONObject(resp);
                        if(object != null){
                            double costo_metro = object.getDouble("costo_metro");
                            double costo_minuto = object.getDouble("costo_minuto");
                            double costo_basico = object.getDouble("costo_basico");
                            mont = costo_basico + (costo_metro * sum ) + ((sum/500)*costo_minuto);
                        }else {
                            return;
                        }
                        int montoaux = (int) mont;
                        monto.setText("Monto aproximado: " +(montoaux-2)+" - "+(montoaux+2));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(points.get(0));
                builder.include(points.get(points.size()-1));
                LatLngBounds bounds=builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                googleMap.moveCamera(cu);
                //sum = metros
            }
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void mostar_button(int tipo) {
            switch (tipo) {
            case 1:
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                btn_pedir_super.setVisibility(View.VISIBLE);
                break;
            case 3:
                btn_pedir_maravilla.setVisibility(View.VISIBLE);
                break;
            case 4:
                btn_pedir_togo.setVisibility(View.VISIBLE);
                linearLayoutPedir.setVisibility(View.GONE);
                linearLayoutTogo.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void calculando_ruta(View view , int tipo){
        selected=null;
        if(mAutocompleteTextView.getTag()!= null && mAutocompleteTextView2.getTag()!=null){
            LatLng latlng1=(LatLng) mAutocompleteTextView.getTag();
            LatLng latlng2=(LatLng) mAutocompleteTextView2.getTag();
            inicio=latlng1;
            fin=latlng2;
            String url = obtenerDireccionesURL(latlng1,latlng2);
            DownloadTask downloadTask= new DownloadTask();
            downloadTask.execute(url);

            tipo_carrera = tipo;
            //ocultado
            ll_ubic.setVisibility(View.GONE);
            iv_marker.setVisibility(View.GONE);
            recyclerView.setVisibility(view.GONE);
            view.setVisibility(View.GONE);
            googleMap.addMarker(new MarkerOptions().position(latlng1).title("INICIO").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
            googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latlng1);
            builder.include(latlng2);
            LatLngBounds bounds=builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
            googleMap.moveCamera(cu);
            linear_confirm.setVisibility(View.VISIBLE);
            //aspdjapsd
            mostraConfirmar(tipo);
        }
    }

    private void mostraConfirmar(int valor){
        switch (valor){
            case 1:
                layoutButon.setVisibility(View.VISIBLE);
                icono1.setVisibility(View.VISIBLE);
                break;
            case 2:
                layoutButon.setVisibility(View.VISIBLE);
                icono2.setVisibility(View.VISIBLE);
                break;
            case 3:
                layoutButon.setVisibility(View.VISIBLE);
                icono3.setVisibility(View.VISIBLE);
                break;
            case 4:
                layoutButon.setVisibility(View.VISIBLE);
                icono4.setVisibility(View.VISIBLE);
                break;
            case 5:
                layoutButon.setVisibility(View.VISIBLE);
                icono5.setVisibility(View.VISIBLE);
                break;
            case 6:
                layoutButon.setVisibility(View.VISIBLE);
                icono6.setVisibility(View.VISIBLE);
                break;
            case 7:
                layoutButon.setVisibility(View.VISIBLE);
                icono7.setVisibility(View.VISIBLE);
                break;
        }
    }


    public class validar_precio extends AsyncTask<Void, String, String> {
        private int id;
        public validar_precio(int id ){
            this.id=id;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_costo");
            parametros.put("id",id+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
        }

    }


    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario");
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
            if(success==null){
                Toast.makeText(PedirSieteMap.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
            }else{
                if (!success.isEmpty()){
                    try {
                        JSONObject usr = new JSONObject(success);
                        if(usr.getString("exito").equals("si")) {
                            SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("usr_log", usr.toString());
                            editor.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
