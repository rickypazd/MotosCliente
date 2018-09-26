package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.ricardopazdemiquel.moviles.Adapter.AdaptadorSieteEstandar;
import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_pedidos_togo;
import com.example.ricardopazdemiquel.moviles.Dialog.Confirmar_viaje_Dialog;
import com.example.ricardopazdemiquel.moviles.Dialog.Confirmar_viaje_Dialog2;
import com.example.ricardopazdemiquel.moviles.Fragment.Producto_togo_Activity;
import com.example.ricardopazdemiquel.moviles.Fragment.SetupViewPager_fragment;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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

public class PedirSieteMap extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private BroadcastReceiver broadcastReceiverMessage;
    MapView mMapView;
    private GoogleMap googleMap;
    private boolean entroLocation = false;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoCompleteTextView mAutocompleteTextView2;
    private AutoCompleteTextView selected;
    private TextView monto;
    private TextView tv_cantidad;
    private Button btn_confirmar;
    private ImageView iv_marker;
    private LinearLayout ll_ubic;
    private LinearLayout linear_confirm;
    private LinearLayout linearLayoutPedir;
    private LinearLayout linearLayoutTogo;
    private LinearLayout linearLayoutcarga;
    private ConstraintLayout layoutButon;
    private ConstraintLayout btn_estandar_recicler;
    private LatLng inicio;
    private LatLng fin;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private LinearLayout recyclerView;
    private int tipo_pago;
    private BottomSheetBehavior bottomSheetBehavior;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.9720));

    JSONObject usr_log;
    //inicializamos los botones para pedir siete y el tipo de carrera
    private ImageView btn_pedir_super, btn_pedir_maravilla;

    //iniciamos los buton del view pager
    private static final int MAX_STEP = 4;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int logo_array[] = {
            R.drawable.background_siete_estadar,
            R.drawable.background_siete_4x4,
            R.drawable.background_siete_camioneta,
            R.drawable.backgroud_tres_filas
    };

    private int tipo_carrera;

    double mont;

    private Button btn_elegir_destino;
    private Boolean listo=false;
    Fragment fragment_favoritos = null;
    Fragment fragment_historial = null;
    android.support.v4.app.Fragment SetupViewPager_fragment = null;

    double longitudeGPS= -63.182033;
    double latitudeGPS=-17.783274;
    private Button btn_ver_listo;
    private LatLng latlngtemp;
    Toolbar toolbar;

    public PedirSieteMap() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_siete_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        ll_ubic = findViewById(R.id.linearLayoutPedir);

        linearLayoutPedir = findViewById(R.id.linearLayoutPedir);
        linearLayoutcarga=findViewById(R.id.cargando);
        iv_marker = findViewById(R.id.ivmarker);
        //esto es prueba
        longitudeGPS = getIntent().getDoubleExtra("lng", 0);
        latitudeGPS = getIntent().getDoubleExtra("lat", 0);
        tipo_carrera = getIntent().getIntExtra("tipo", 0);
        mGoogleApiClient = new GoogleApiClient.Builder(PedirSieteMap.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        btn_pedir_super = findViewById(R.id.btn_pedir_super);
        btn_pedir_maravilla = findViewById(R.id.btn_pedir_maravilla);
        btn_elegir_destino= findViewById(R.id.btn_elegir_destino);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        // adding bottom dots
        bottomProgressDots(0);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin_overlap_payment));
        viewPager.setOffscreenPageLimit(MAX_STEP);

        recyclerView = findViewById(R.id.reciclerView);
        /*recyclerView.setHasFixedSize(true);
        LinearLayoutManager mylinear = new LinearLayoutManager(this);
        mylinear.setOrientation(LinearLayoutManager.HORIZONTAL);
        try {
            JSONArray arr = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj1.put("id", 1);
            obj1.put("nombre", R.drawable.background_siete_estadar);
            arr.put(obj1);
            JSONObject obj2 = new JSONObject();
            obj2.put("id", 5);
            obj2.put("nombre", R.drawable.background_siete_4x4);
            arr.put(obj2);
            JSONObject obj3 = new JSONObject();
            obj3.put("id", 6);
            obj3.put("nombre", R.drawable.background_siete_camioneta);
            arr.put(obj3);
            JSONObject obj4 = new JSONObject();
            obj4.put("id", 7);
            obj4.put("nombre", R.drawable.backgroud_tres_filas);
            arr.put(obj4);
            AdaptadorSieteEstandar ada = new AdaptadorSieteEstandar(arr, this, PedirSieteMap.this);
            recyclerView.setAdapter(ada);
            recyclerView.setLayoutManager(mylinear);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        btn_pedir_super.setOnClickListener(this);
        btn_pedir_maravilla.setOnClickListener(this);
        btn_elegir_destino.setOnClickListener(this);

        View view = findViewById(R.id.button_sheetss);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        //bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BehaviorCuston.STATE_EXPANDED);

        SetupViewPager_fragment = new SetupViewPager_fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment, SetupViewPager_fragment).commit();

        mostar_button(tipo_carrera);

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setOnFocusChangeListener(this);
        mAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected=(AutoCompleteTextView) v;

                AutoCompleteTextView auto =(AutoCompleteTextView)v;
                selected.setText("");
                bottomSheetBehavior.setState(BehaviorCuston.STATE_EXPANDED);
            }
        });
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        mAutocompleteTextView2 = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView2);
        mAutocompleteTextView2.setOnFocusChangeListener(this);
        mAutocompleteTextView2.setThreshold(3);
        mAutocompleteTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected=(AutoCompleteTextView) v;
                bottomSheetBehavior.setState(BehaviorCuston.STATE_EXPANDED);
                AutoCompleteTextView auto =(AutoCompleteTextView)v;
                selected.setText("");
            }
        });
        mAutocompleteTextView2.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView2.setAdapter(mPlaceArrayAdapter);

        usr_log = getUsr_log();

        if (usr_log == null) {
            Intent intent = new Intent(PedirSieteMap.this, LoginCliente.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        active=false;

        mMapView = findViewById(R.id.mapviewPedirSiete);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        btn_ver_listo=findViewById(R.id.verlisto);
        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeGPS, longitudeGPS), 18);
                googleMap.animateCamera(cu);
                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(PedirSieteMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PedirSieteMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if (!entroLocation) {
                            if(selected!=mAutocompleteTextView2){
                                entroLocation = true;
                                linearLayoutcarga.setVisibility(View.GONE);
                                selected.setTag(new LatLng(location.getLatitude(), location.getLongitude()));
                                mMap.clear();
                                selected.setText(getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                                mAutocompleteTextView2.requestFocus();
                                selected.dismissDropDown();
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14);
                                googleMap.animateCamera(cu);
                            }

                        }
                        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {

                                LatLng center = googleMap.getCameraPosition().target;
                                if(listo){
                                    latlngtemp=center;
                                    String addres=getCompleteAddressString(latlngtemp.latitude, latlngtemp.longitude);
                                    selected.setText(addres);
                                    selected.dismissDropDown();
                                    }

                            }
                        });
                    }
                });
                btn_ver_listo.setVisibility(View.GONE);
                listo=false;
                googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int i) {
                        if(i== GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                            listo=true;
                            btn_ver_listo.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        btn_ver_listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centrar();
                listo=false;
                btn_ver_listo.setVisibility(View.GONE);
                selected.setTag(latlngtemp);
                String addres=getCompleteAddressString(latlngtemp.latitude, latlngtemp.longitude);
                selected.setText(addres);
                selected.dismissDropDown();
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
            layoutParams.setMargins(0, 220, 10, 0);

            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);
        }
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.colorPrimaryDark2), PorterDuff.Mode.SRC_IN);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_payment, container, false);
             ImageView ima = ((ImageView) view.findViewById(R.id.card_logo));
            ima.setImageResource(logo_array[position]);
            ima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            CalcularRuta(1);
                            break;
                        case 1:
                            CalcularRuta(5);
                            break;
                        case 2:
                            CalcularRuta(6);
                            break;
                        case 3:
                            CalcularRuta(7);
                            break;
                    }
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return MAX_STEP;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }



    private void centrar(){
        LatLng center = googleMap.getCameraPosition().target;
        selected.setTag(center);
        String addres=getCompleteAddressString(center.latitude, center.longitude);
        selected.setText(addres);
        selected.dismissDropDown();
        calculando_ruta(10,addres,center.latitude, center.longitude);
        listo=false;
        btn_ver_listo.setVisibility(View.GONE);
    }
    private Boolean active;
    public void close() {
        if (bottomSheetBehavior instanceof BehaviorCuston) {
            ((BehaviorCuston) bottomSheetBehavior).setLocked(true);

        }
    }

    public void open() {
        if (bottomSheetBehavior instanceof BehaviorCuston) {
            ((BehaviorCuston) bottomSheetBehavior).setLocked(false);
        }
    }


    private void notificacionReciber(Intent intent) {

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
        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }

    }

    public  void ok(){
        try {
            ok_predir_viaje();
        } catch (JSONException e) {
            e.printStackTrace();
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
            case R.id.btn_pedir_super:
                CalcularRuta(4);
                break;
            case R.id.btn_pedir_maravilla:
                CalcularRuta(3);
                break;
            case R.id.btn_elegir_destino:
                iv_marker.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
        }
    }

    public void Verificar_tipo_siete(int tipo,String nombre , Double lat ,Double lng ){
        switch (tipo){
            case 1:
                calculando_ruta(tipo_carrera ,nombre,lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
            case 2:
                break;
            case 3:
                Aux_CalcularRuta(tipo_carrera ,lat,lng);
                break;
            case 4:
                Aux_CalcularRuta(tipo_carrera, lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
            case 5:
                calculando_ruta(tipo_carrera ,nombre  ,lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
            case 6:
                calculando_ruta(tipo_carrera ,nombre  ,lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
            case 7:
                calculando_ruta(tipo_carrera ,nombre  ,lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
            case 10:
                calculando_ruta(tipo ,nombre  ,lat,lng);
                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                break;
        }
    }

    ///AL SELECCIONAR EL TIPO DEL SIETE EN EL MAPA
    public void CalcularRuta(int tipo_carrera){
       Object tag=mAutocompleteTextView2.getTag();
        Object taga=mAutocompleteTextView.getTag();
        if(mAutocompleteTextView.getTag()!=null && mAutocompleteTextView2.getTag()!=null){
            Intent intent = new Intent(PedirSieteMap.this, Calcular_ruta_activity.class);
            LatLng latlng1=(LatLng) mAutocompleteTextView.getTag();
            LatLng latlng2=(LatLng) mAutocompleteTextView2.getTag();
            intent.putExtra("tipo", tipo_carrera);
            intent.putExtra("lng", longitudeGPS);
            intent.putExtra("lat", latitudeGPS);
            intent.putExtra("latinicio", latlng1.latitude);
            intent.putExtra("lnginicio", latlng1.longitude);
            intent.putExtra("latfinal", latlng2.latitude);
            intent.putExtra("lngfinal", latlng2.longitude);

            float[] results = new float[1];
            float sum = 0;

            Location.distanceBetween(
                    inicio.latitude,
                    inicio.longitude,
                    fin.latitude,
                    fin.longitude,
                    results);
            if(results[0]>Float.valueOf(getString(R.string.maxima_km_busqueda))){
                Toast.makeText(PedirSieteMap.this,"Por favor marque dentro de Santa Cruz.",Toast.LENGTH_LONG).show();
                return;
            }else{
                startActivity(intent);
            }
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            Toast.makeText(PedirSieteMap.this,"Seleccione una ubicación valida.",Toast.LENGTH_LONG).show();
        }
    }

    public void Aux_CalcularRuta(int tipo_carrera, Double lat ,Double lng ){
        if(mAutocompleteTextView.getText().toString().length() != 0 && mAutocompleteTextView2.getText().toString().length() != 0){
            Intent intent = new Intent(PedirSieteMap.this, Calcular_ruta_activity.class);
            LatLng latlng1=(LatLng) mAutocompleteTextView.getTag();
            intent.putExtra("tipo", tipo_carrera);
            intent.putExtra("lng", longitudeGPS);
            intent.putExtra("lat", latitudeGPS);
            intent.putExtra("latinicio", latlng1.latitude);
            intent.putExtra("lnginicio", latlng1.longitude);
            intent.putExtra("latfinal", lat);
            intent.putExtra("lngfinal", lng);
            float[] results = new float[1];
            float sum = 0;
            Location.distanceBetween(
                    inicio.latitude,
                    inicio.longitude,
                    fin.latitude,
                    fin.longitude,
                    results);
            if(results[0]>  Float.valueOf(getString(R.string.maxima_km_busqueda))){
                Toast.makeText(PedirSieteMap.this,"Por favor marque dentro de Santa Cruz.",Toast.LENGTH_LONG).show();
                return;
            }else{
                startActivity(intent);
            }
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            Toast.makeText(PedirSieteMap.this,"Seleccione una ubicación valida.",Toast.LENGTH_LONG).show();
        }
    }


    public void calculando_ruta(int tipo, String nombre , Double lat ,Double lng){
            addpositionFavorito(nombre , lat , lng);
        closeSoftKeyBoard();
        btn_ver_listo.setVisibility(View.GONE);
        if(selected!=mAutocompleteTextView){
            if(mAutocompleteTextView.getTag()!= null){
                googleMap.clear();
                LatLng latlng1=(LatLng) mAutocompleteTextView.getTag();
                LatLng latlng2= new LatLng(lat , lng);
                inicio=latlng1;
                fin=latlng2;
                String url = obtenerDireccionesURL(latlng1,latlng2);
                DownloadTask downloadTask= new DownloadTask();
                downloadTask.execute(url);
                tipo_carrera = tipo;
                googleMap.addMarker(new MarkerOptions().position(latlng1).title("INICIO").icon(Inicio_bitmapDescriptorFromVector(this , R.drawable.asetmar)));
                googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(Fin_bitmapDescriptorFromVector(this, R.drawable.asetmar)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(latlng1);
                builder.include(latlng2);
                LatLngBounds bounds=builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,(int)(width*0.6),(int)(height*0.6),100);
               // googleMap.setPadding(200,200,200,200);

                googleMap.moveCamera(cu);


                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                listo=true;

            }
        }else{
            if(mAutocompleteTextView2.getTag()!= null){
                googleMap.clear();
                LatLng latlng1=(LatLng) mAutocompleteTextView2.getTag();
                LatLng latlng2= new LatLng(lat , lng);
                inicio=latlng1;
                fin=latlng2;
                String url = obtenerDireccionesURL(latlng1,latlng2);
                DownloadTask downloadTask= new DownloadTask();
                downloadTask.execute(url);
                tipo_carrera = tipo;
                googleMap.addMarker(new MarkerOptions().position(latlng1).title("INICIO").icon(Inicio_bitmapDescriptorFromVector(this , R.drawable.asetmar)));
                googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(Fin_bitmapDescriptorFromVector(this, R.drawable.asetmar)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(latlng1);
                builder.include(latlng2);
                LatLngBounds bounds=builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,(int)(width*0.6),(int)(height*0.6),100);
             //   googleMap.setPadding(200,200,200,200);

                    googleMap.moveCamera(cu);


                bottomSheetBehavior.setState(BehaviorCuston.STATE_HIDDEN);
                listo=true;
            }
        }

    }

    private BitmapDescriptor Inicio_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_icon_pointer_map);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor Fin_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_icon_pointer_map2);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private String get_localizacion(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(PedirSieteMap.this, Locale.getDefault());
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


    public void addpositionFavorito( String nombre , Double lat ,Double lng){
        LatLng lat1 = new LatLng(lat ,lng);
        //String nombre  = get_localizacion(lat ,lng);

        selected.setText(nombre,false);
        selected.setTag(lat1);
    }

    //ESTE AUTO COMPLETA
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
//ESTE COLOCA EL TEXTO AL TEXTVIEW
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
            //String nombre=place.getAddress().toString();
            LatLng lat=place.getLatLng();
            selected.setText(place.getAddress(),false);
            selected.setTag(lat);
            calculando_ruta(0,place.getAddress()+"",lat.latitude,lat.longitude);
            //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18);
            //googleMap.animateCamera(cu);

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
            bottomSheetBehavior.setState(BehaviorCuston.STATE_EXPANDED);
            iv_marker.setVisibility(View.VISIBLE);
            selected.setText("");

        }
    }
    public void closeSoftKeyBoard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
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

                //polylineOption
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.rgb(93,56,146));
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
                setTitle("A dónde vamos");
                toolbar.setTitleTextColor(Color.WHITE);
                break;
            case 3:
                btn_pedir_maravilla.setVisibility(View.VISIBLE);
                setTitle("A dónde vamos");
                toolbar.setTitleTextColor(Color.WHITE);
                break;
            case 4:
                btn_pedir_super.setVisibility(View.VISIBLE);
                setTitle("A dónde vamos");
                toolbar.setTitleTextColor(Color.WHITE);
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
