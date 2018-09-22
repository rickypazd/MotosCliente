package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_pedidos_togo;
import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_producto_togo;
import com.example.ricardopazdemiquel.moviles.Dialog.Confirmar_viaje_Dialog;
import com.example.ricardopazdemiquel.moviles.Dialog.Confirmar_viaje_Dialog2;
import com.example.ricardopazdemiquel.moviles.Dialog.Producto_togo_Dialog;
import com.example.ricardopazdemiquel.moviles.Fragment.Producto_togo_Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import utiles.BehaviorCuston;
import utiles.Contexto;
import utiles.DirectionsJSONParser;
import utiles.Token;

public class PedirSieteTogo extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    MapView mMapView;
    private GoogleMap googleMap;
    private boolean entroLocation=false;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView selected;
    private TextView monto;
    private ListView lista_productos;
    private TextView tv_cantidad;
    private Button btn_confirmar;
    private ImageView iv_marker;
    private LinearLayout ll_ubic;
    private LinearLayout linear_confirm;
    private LinearLayout linearLayoutTogo;
    private ConstraintLayout layoutButon;
    private LatLng inicio;
    private LatLng fin;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private int tipo_pago;
    private BottomSheetBehavior bottomSheetBehavior;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.9720));

    JSONObject usr_log;
    //inicializamos los botones para pedir siete togo y el tipo de carrera
    private ImageView btn_pedir_togo ;
    private int tipo_carrera;
    private RadioButton radio_efectivo;
    private RadioButton radio_credito;
    // inicializamos los iconos de confirmar carrera
    private TextView icono2 ;
    double mont;
    private AutoCompleteTextView text_direccion_togo;
    private ImageView btn_agregar_producto;
    private Adapter_pedidos_togo adapter;

    public PedirSieteTogo() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_siete_togo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        setTitle("Siete Togo");
        toolbar.setTitleTextColor(Color.WHITE);

        lista_productos=findViewById(R.id.lista_productos);
        tv_cantidad=findViewById(R.id.tv_cantidad);
        linearLayoutTogo = findViewById(R.id.linearLayoutTogo);
        layoutButon=findViewById(R.id.ll_boton);
        iv_marker=findViewById(R.id.ivmarker);
        monto = findViewById(R.id.tv_monto);
        text_direccion_togo = findViewById(R.id.text_direccion_togo);
        btn_agregar_producto = findViewById(R.id.btn_agregar_producto);
        btn_agregar_producto.setOnClickListener(this);
        View view =findViewById(R.id.bottom_sheet);
        bottomSheetBehavior= BottomSheetBehavior.from(view);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                   Drawable image = getResources().getDrawable(R.drawable.ic_icon_up_arrow);
                   tv_cantidad.setCompoundDrawablesRelative(image,null,null,null);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Drawable image = getResources().getDrawable( R.drawable.ic_icon_down_arrow);
                    tv_cantidad.setCompoundDrawablesRelative(image,null,null,null);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        lista_productos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if (bottomSheetBehavior instanceof BehaviorCuston) {
                        ((BehaviorCuston) bottomSheetBehavior).setLocked(true);

                    }
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    if (bottomSheetBehavior instanceof BehaviorCuston) {
                        ((BehaviorCuston) bottomSheetBehavior).setLocked(false);
                    }
                }

                return false;
            }
        });

        final double longitudeGPS=getIntent().getDoubleExtra("lng",0);
        final double latitudeGPS=getIntent().getDoubleExtra("lat",0);

        tipo_carrera = getIntent().getIntExtra("tipo",0);

        mGoogleApiClient = new GoogleApiClient.Builder(PedirSieteTogo.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        btn_pedir_togo = findViewById(R.id.btn_pedir_togo);
        radio_efectivo = findViewById(R.id.radio_efectivo);
        radio_credito = findViewById(R.id.radio_credito);
        radio_efectivo.setOnClickListener(this);
        radio_credito.setOnClickListener(this);

        icono2 = findViewById(R.id.icono2);
        btn_pedir_togo.setOnClickListener(this);

        linear_confirm=findViewById(R.id.linear_confirm);

        mostar_button(tipo_carrera);

        text_direccion_togo = findViewById(R.id.text_direccion_togo);
        text_direccion_togo.setOnFocusChangeListener(this);
        text_direccion_togo.setThreshold(3);
        text_direccion_togo.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        text_direccion_togo.setAdapter(mPlaceArrayAdapter);

        usr_log = getUsr_log();

        if (usr_log == null) {
            Intent intent = new Intent(PedirSieteTogo.this, LoginCliente.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        JSONArray arr = getProductosPendientes();
        if(arr!=null){
            adapter = new Adapter_pedidos_togo(PedirSieteTogo.this,arr);
            lista_productos.setAdapter(adapter);
            tv_cantidad.setText("Productos ("+arr.length()+")");
        }

        btn_confirmar= findViewById(R.id.btn_confirmar);
        btn_confirmar.setOnClickListener(this);

        mMapView = findViewById(R.id.mapviewPedirSiete);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeGPS, longitudeGPS), 14);
                googleMap.animateCamera(cu);
                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(PedirSieteTogo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PedirSieteTogo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                        if (selected != null && entroLocation) {
                            LatLng center = googleMap.getCameraPosition().target;
                            selected.setTag(center);
                            mMap.clear();
                            if (text_direccion_togo.getTag() != null) {
                                LatLng latlng2 = (LatLng) text_direccion_togo.getTag();
                                fin=latlng2;
                               // googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)).anchor(0.5f,0.5f));
                            }
                            selected.setText(getCompleteAddressString(center.latitude, center.longitude));

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
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 200, 10, 0);

            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);
        }
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

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
    protected void onResume() {
        super.onResume();
        /*if(tipo_carrera==2){
            cargartogo();
        }*/

    }

    public void ok_pedir_viaje() throws JSONException {
        JSONArray arr = getProductosPendientes();
        Intent inte = new Intent(PedirSieteTogo.this, PidiendoSiete.class);
        inte.putExtra("latFin", fin.latitude + "");
        inte.putExtra("lngFin", fin.longitude + "");
        inte.putExtra("token", Token.currentToken);
        inte.putExtra("id_usr", usr_log.getInt("id") + "");
        inte.putExtra("tipo", tipo_carrera + "");
        inte.putExtra("tipo_pago", tipo_pago + "");
        inte.putExtra("productos", arr + "");
        startActivity(inte);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pedir_togo:
                //calculando_ruta(view , tipo_carrera);
                mostraConfirmar(tipo_carrera);
                break;
            case R.id.btn_agregar_producto:
                //Intent intent =  new Intent(PedirSieteTogo.this, Producto_togo_Activity.class);
                //startActivity(intent);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                new Producto_togo_Dialog(new JSONObject() , 0 , 2).show(fragmentManager, "Dialog");
                break;
            case R.id.btn_confirmar:
                Confimar_viaje();
                break;
        }
    }

    public void InsertList(JSONObject object){
        JSONArray arr = getProductosPendientes();
        if(arr==null){
            arr= new JSONArray();
        }
        arr.put(object);
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("productos_pendientes", arr.toString());
        editor.commit();
        Adapter_pedidos_togo adapters = new Adapter_pedidos_togo(PedirSieteTogo.this,arr);
        lista_productos.setAdapter(adapters);
        tv_cantidad.setText("Productos ("+arr.length()+")");

    }

    public void UpdateList(JSONObject object , int pos) throws JSONException {
        JSONArray arr = getProductosPendientes();
        if(arr==null){
            arr= new JSONArray();
        }else{
            arr.put(pos,object);
            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("productos_pendientes", arr.toString());
            editor.commit();
            Adapter_pedidos_togo adapters = new Adapter_pedidos_togo(PedirSieteTogo.this,arr);
            lista_productos.setAdapter(adapters);
            tv_cantidad.setText("Productos ("+arr.length()+")");
        }

    }

    public void removeItem(int pos){

        JSONArray arr = getProductosPendientes();
        if(arr==null){
            arr= new JSONArray();
        }else{
            arr.remove(pos);
            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("productos_pendientes", arr.toString());
            editor.commit();
            Adapter_pedidos_togo adapters = new Adapter_pedidos_togo(PedirSieteTogo.this,arr);
            lista_productos.setAdapter(adapters);
            tv_cantidad.setText("Productos ("+arr.length()+")");
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
                //StringBuilder strReturnedAddress = new StringBuilder("");

                strAdd=returnedAddress.getThoroughfare();
                if(strAdd==null )
                strAdd=returnedAddress.getFeatureName();

                //  Log.w("My Current loction addr", strReturnedAddress.toString());
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
                        Toast.makeText(PedirSieteTogo.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
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
            case 2:
                btn_pedir_togo.setVisibility(View.VISIBLE);
                cargartogo();
                linearLayoutTogo.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void Confimar_viaje() {
        try {
            String id = usr_log.getString("id");
            String resp =new User_getPerfil(id).execute().get();
            if(text_direccion_togo.getText().toString().length() != 0){
            if(resp==null){
                Toast.makeText(PedirSieteTogo.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
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
                                new Confirmar_viaje_Dialog(tipo_carrera).show(fragmentManager, "Dialog");
                                //esta en deuda , aler se cobrara el monto + viej
                                acept=false;
                            }
                        }
                        if(acept){
                            ok_pedir_viaje();
                        }
                    }
                }
            }
            }else{
                Toast.makeText(PedirSieteTogo.this,"Seleccione una ubicación valida.",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void cargartogo(){
        JSONArray arr = getProductosPendientes();
        if(arr!=null){
            Adapter_pedidos_togo adapter = new Adapter_pedidos_togo(PedirSieteTogo.this,arr);
            lista_productos.setAdapter(adapter);
            tv_cantidad.setText("Productos ("+arr.length()+")");
        }
    }

   /* public void calculando_ruta(View view , int tipo){
        selected=null;
        if(text_direccion_togo.getTag()!= null && text_direccion_togo.getTag()!=null){
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
    }*/

    private void mostraConfirmar(int valor){
        switch (valor){
            case 2:
                layoutButon.setVisibility(View.VISIBLE);
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
                Toast.makeText(PedirSieteTogo.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
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
}
