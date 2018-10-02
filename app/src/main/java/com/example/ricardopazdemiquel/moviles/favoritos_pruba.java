package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_favoritos;
import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_pedidos_togo;
import com.example.ricardopazdemiquel.moviles.Dialog.Add_ubicacion_favoritos_Dialog;
import com.example.ricardopazdemiquel.moviles.Dialog.Producto_togo_Dialog;
import com.example.ricardopazdemiquel.moviles.Fragment.List_historial_fragment;
import com.example.ricardopazdemiquel.moviles.Fragment.List_historial_ubicacion_fragment;
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

public class favoritos_pruba extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks
        ,AdapterView.OnItemClickListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private boolean entroLocation=false;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView selected;
    private TextView monto;
    private ImageView iv_marker;
    private LatLng fin;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.9720));
    JSONObject usr_log;
    double mont;

    private LinearLayout container_frame;
    private AutoCompleteTextView text_direccion_togo;
    private Button btn_elegir_destino;
    private Button btn_agregar;
    Fragment fragment_historial  = null;
    private ListView lv_List_favoritos;
    private Adapter_favoritos adapter;

    public favoritos_pruba() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_clientes);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        iv_marker=findViewById(R.id.ivmarker);
        text_direccion_togo = findViewById(R.id.text_direccion_togo);
        container_frame = findViewById(R.id.container_frame);
        btn_elegir_destino = findViewById(R.id.btn_elegir_destino);
        btn_agregar = findViewById(R.id.btn_agregar);
        btn_elegir_destino.setOnClickListener(this);
        btn_agregar.setOnClickListener(this);

        lv_List_favoritos = findViewById(R.id.lv_List_favoritos);
        lv_List_favoritos.setOnItemClickListener(this);

        cargar();

        //esto es una pruebaaaa de escritura


       /* View view =findViewById(R.id.bottom_sheet);
        bottomSheetBehavior= BottomSheetBehavior.from(view);
        bottomSheetBehavior.setHideable(false);
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
        });*/

        final double longitudeGPS=getIntent().getDoubleExtra("lng",0);
        final double latitudeGPS=getIntent().getDoubleExtra("lat",0);

        mGoogleApiClient = new GoogleApiClient.Builder(favoritos_pruba.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        text_direccion_togo = findViewById(R.id.text_direccion_togo);
        text_direccion_togo.setOnFocusChangeListener(this);
        text_direccion_togo.setThreshold(3);
        text_direccion_togo.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        text_direccion_togo.setAdapter(mPlaceArrayAdapter);

        usr_log = getUsr_log();

        if (usr_log == null) {
            Intent intent = new Intent(favoritos_pruba.this, LoginCliente.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

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
                if (ActivityCompat.checkSelfPermission(favoritos_pruba.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(favoritos_pruba.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                           //     googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)).anchor(0.5f,0.5f));
                            }
                            selected.setText(getCompleteAddressString(center.latitude, center.longitude));

                        }


                    }
                });

            }
        });

        /*if (mMapView != null && mMapView.findViewById(Integer.parseInt("1")) != null) {
            ImageView locationButton = (ImageView) ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 150, 0);
            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);

        }*/
       /* mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });*/

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.lv_List_favoritos) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_producto, menu);
        }
    }

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
                if(pos == 0 ){
                    Toast.makeText(favoritos_pruba.this , "no se puede eliminar." ,Toast.LENGTH_LONG).show();
                }else{
                    removeItem(pos);
                }
                break;
            case R.id.action_update_producto:
                if(pos == 0 ){
                    Toast.makeText(favoritos_pruba.this , "no se puede editar." ,Toast.LENGTH_LONG).show();
                }else{
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    new Add_ubicacion_favoritos_Dialog(obj,pos,1).show(fragmentManager, "Dialog");
                }
                break;
        }
        return true;
    }

    public void removeItem(final int pos){
        /*new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .show();*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se eliminara de tu lista de favoritos.")
                .setTitle("Eliminar")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        adapter.removeiten(pos);
                        adapter.notifyDataSetChanged();
                        JSONArray arr = adapter.getArray();
                        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("lista_favoritos", arr.toString());
                        editor.commit();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void UpdateList(JSONObject object , int pos) throws JSONException {
        JSONArray arr = get_list_Favoritos();
        if(arr==null){
            arr= new JSONArray();
        }else{
            arr.put(pos,object);
            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("lista_favoritos", arr.toString());
            editor.commit();
            Adapter_favoritos adapters = new Adapter_favoritos(favoritos_pruba.this,arr);
            lv_List_favoritos.setAdapter(adapters);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }
    private void cargar(){
        //carga un SharedPreferences de favoritos o crea uno vacio
        JSONArray productos = get_list_Favoritos();
        if(productos==null)
        {
            SharedPreferences preferencias = favoritos_pruba.this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            productos=new JSONArray();
            editor.putString("c", productos.toString());
            editor.commit();
        }
        adapter = new Adapter_favoritos(favoritos_pruba.this,productos);
        lv_List_favoritos.setAdapter(adapter);
                   //  lv_List_favoritos.setOn;
        JSONObject obj = new JSONObject();
    }

    public JSONArray get_list_Favoritos() {
        SharedPreferences preferencias = favoritos_pruba.this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
        android.app.FragmentManager fragmentManager = getFragmentManager();
        switch (view.getId()) {
            case R.id.btn_elegir_destino:
                container_frame.setVisibility(View.GONE);
                btn_agregar.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_agregar:
                JSONObject obj = new JSONObject();
                Double latFin = fin.latitude;
                Double lngFin = fin.longitude;
                try {
                    obj.put("user_id",getUsr_log().getInt("id"));
                    obj.put("latFin",latFin);
                    obj.put("lngFin",lngFin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Add_ubicacion_favoritos_Dialog(obj , 0 ,2).show(fragmentManager, "Dialog");
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        registerForContextMenu(lv_List_favoritos);
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
                cargartogo();
                break;
        }
    }

    private void cargartogo(){
        JSONArray arr = getProductosPendientes();
        if(arr!=null){
            Adapter_pedidos_togo adapter = new Adapter_pedidos_togo(favoritos_pruba.this,arr);
            //lista_productos.setAdapter(adapter);
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
