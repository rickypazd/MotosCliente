package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Dialog.Cancelar_viaje_Dialog;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Timer;
import java.util.concurrent.ExecutionException;


import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Constants;
import utiles.Contexto;
import utiles.DirectionsJSONParser;


public class EsperandoConductor extends AppCompatActivity implements View.OnClickListener{
    MapView mMapView;
    private GoogleMap googleMap;
    JSONObject json_carrera;
    private JSONObject usr_log;
    private LinearLayout Container_cancelar;
    private CoordinatorLayout Container_verPerfil;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView text_nombreConductor;
    private ImageView img_foto;
    private TextView text_nombreAuto;
    private TextView text_numeroPlaca;
    private TextView text_Viajes;
    private Button btn_cancelar_viaje;

    private TextView cont_amable , cont_buena_ruta , cont_auto_limpio , text_ultimo_mensaje;


    private Button btn_enviar_mensaje;
    private Button btn_llamar;
    JSONObject Json_cancelarViaje;
//    private LinearLayout perfil_condutor;

    //añadiendo los broadcaast
    private BroadcastReceiver broadcastReceiverMessage;
    private BroadcastReceiver broadcastReceiverMessageconductor;
    private BroadcastReceiver broadcastReceiverInicioCarrera;
    private BroadcastReceiver broadcastReceiverFinalizoCarrera;
    private BroadcastReceiver broadcastReceiverCanceloCarrera;

    private final static int TIPO_CANCELACION = 2;
    private final static int ID_TIPO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esperando_conductor);

        text_nombreConductor = findViewById(R.id.text_nombreConductor);
        btn_llamar = findViewById(R.id.btn_llamar);
        btn_enviar_mensaje = findViewById(R.id.btn_enviar_mensaje);
        text_nombreAuto = findViewById(R.id.text_nombreAuto);
        text_numeroPlaca = findViewById(R.id.text_numeroPlaca);
        text_Viajes= findViewById(R.id.text_Viajes);
        img_foto = findViewById(R.id.img_foto);
        Container_cancelar = findViewById(R.id.Container_cancelar);
        Container_verPerfil = findViewById(R.id.Container_verPerfil);
        btn_cancelar_viaje = findViewById(R.id.btn_cancelar_viaje);
        cont_amable = findViewById(R.id.cont_amable);
        cont_buena_ruta = findViewById(R.id.cont_buena_ruta);
        cont_auto_limpio = findViewById(R.id.cont_auto_limpio);
        text_ultimo_mensaje = findViewById(R.id.text_ultimo_mensaje);


        btn_cancelar_viaje.setOnClickListener(this);

        try {
            json_carrera = new JSONObject(getIntent().getStringExtra("obj_carrera"));
            if(json_carrera.getInt("estado")>=3){
                    conductor_llego(getIntent());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        usr_log=getUsr_log();

        View view =findViewById(R.id.button_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override

            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mMapView=findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(EsperandoConductor.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EsperandoConductor.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                if(json_carrera!=null){
                    hilo();
                }
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


    @Override
    protected void onResume() {
        super.onResume();

        //broadcast  conductor cerca
        if(broadcastReceiverMessage == null){
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    notificacionReciber(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessage,new IntentFilter("conductor_cerca"));

        //broadcast  conductor llego
        if(broadcastReceiverMessageconductor == null){
            broadcastReceiverMessageconductor = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(EsperandoConductor.this,"Tu conductor llegó",Toast.LENGTH_SHORT).show();
                    conductor_llego(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessageconductor,new IntentFilter("conductor_llego"));

        //broadcast  inicio de carrera
        if(broadcastReceiverInicioCarrera == null){
            broadcastReceiverInicioCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Inicio_Carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverInicioCarrera,new IntentFilter("Inicio_Carrera"));

        //Broadcast finalizo carrera
        if(broadcastReceiverFinalizoCarrera == null){
            broadcastReceiverFinalizoCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    finalizo_carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverFinalizoCarrera,new IntentFilter("Finalizo_Carrera"));

        // Broadcast Cancelo el viaje el conductor
        if(broadcastReceiverCanceloCarrera == null){
            broadcastReceiverCanceloCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Cancelo_carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverCanceloCarrera,new IntentFilter("cancelo_carrera"));

    }


    private boolean hilo;
    private void hilo(){
        hilo=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(hilo){
                    try {
                        new posicion_conductor().execute();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private String number;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 255: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone();
                } else {
                    System.out.println("El usuario ha rechazado el permiso");
                }
                return;
            }
        }
    }

    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void notificacionReciber(Intent intent){
        Toast.makeText(EsperandoConductor.this,"Tu conductor está cerca.",
                Toast.LENGTH_SHORT).show();
    }

    private void conductor_llego(Intent intent){
        Container_cancelar.setVisibility(View.GONE);
        try {
            new Get_ObtenerPerfilConductor(json_carrera.getString("id")).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Inicio_Carrera(Intent intent){
        Toast.makeText(EsperandoConductor.this,"Tu viaje ha comenzado. Que tengas buen viaje.",
                Toast.LENGTH_SHORT).show();
        new buscar_carrera().execute();
        //perfil_condutor.setVisibility(View.VISIBLE);
    }

    private void finalizo_carrera(Intent intenta){
        Intent intent = new Intent( EsperandoConductor.this, finalizar_viajeCliente.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("carrera",intenta.getStringExtra("carrera"));
        startActivity(intent);
        finish();
    }

    private void Cancelo_carrera(Intent intenta){
        Intent intent = new Intent( EsperandoConductor.this, CanceloViaje_Cliente.class);
        intent.putExtra("id_carrera",intenta.getStringExtra("id_carrera"));
        startActivity(intent);
        finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancelar_viaje:
                new Cancelar_viaje().execute();
                break;
        }
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

    private float dist=0;
    private Marker marauto;
    private Marker mardest;
    private class posicion_conductor extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            try {
                int id=json_carrera.getInt("id");
                param.put("evento","get_pos_conductor_x_id_carrera");
                param.put("id",id+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp==null){
                Toast.makeText(EsperandoConductor.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else{
                if (resp.equals("falso")) {
                    Toast.makeText(EsperandoConductor.this,"Perdimos la conexión con tu conductor.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        JSONObject obj=new JSONObject(resp);
                        LatLng ll1 = new LatLng(obj.getDouble("lat"),obj.getDouble("lng"));
                        LatLng ll2;
                        if(obj.getInt("estado")!=json_carrera.getInt("estado")){
                            int estado=obj.getInt("estado");
                            json_carrera.put("estado",estado);
                            switch (estado){
                                case 1:
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    notificacionReciber(new Intent());
                                    break;
                                case 4:
                                    Inicio_Carrera(new Intent());
                                    break;
                                case 5:
                                    finalizo_carrera(new Intent().putExtra("carrera",json_carrera.toString()));
                                    break;
                                case 6:
                                    break;
                                case 7:
                                    break;
                            }


                        }
                        if(obj.getInt("estado")==4){
                            ll2= new LatLng(json_carrera.getDouble("latfinal"),json_carrera.getDouble("lngfinal"));
                            //cfdfgd
                        }else{
                            ll2=new LatLng(json_carrera.getDouble("latinicial"),json_carrera.getDouble("lnginicial"));
                        }

                        String url = obtenerDireccionesURL(ll1,ll2);
                        float[] results = new float[1];
                        Location.distanceBetween(
                                ll1.latitude,
                                ll1.longitude,
                                ll2.latitude,
                                ll2.longitude,
                                results);
                        if((dist-results[0])> 20 || (dist-results[0])< -20|| dist==0){
                            googleMap.clear();
                            mardest=null;
                            marauto=null;
                            dist=results[0];
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(ll1);
                            builder.include(ll2);
                            LatLngBounds bounds=builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,300);
                            googleMap.moveCamera(cu);
                            DownloadTask downloadTask= new DownloadTask();
                            downloadTask.execute(url);
                        }
                        if(mardest==null){
                            googleMap.addMarker(new MarkerOptions().position(ll2).title("FIN").icon(Fin_bitmapDescriptorFromVector(getApplication(), R.drawable.asetmar)));
                        }else{
                            mardest.setPosition(ll2);
                        }
                        float degre = Float.parseFloat(obj.getString("bearing"));
                        if(marauto==null){

                            marauto=googleMap.addMarker(new MarkerOptions().position(ll1).title("AUTO").rotation(degre).icon(Auto_bitmapDescriptorFromVector(getApplication(), R.drawable.auto)).anchor(0.5f,0.5f));
                        }else{
                            marauto.setPosition(ll1);
                            marauto.setRotation(degre);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }


    private BitmapDescriptor Fin_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_icon_pointer_map3);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(100, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private BitmapDescriptor Auto_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_motoubicasset_2asd);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    private class buscar_carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_carrera_id");
            try {
                param.put("id",json_carrera.getInt("id")+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp == null){
                Toast.makeText(EsperandoConductor.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if (resp.isEmpty()) {
                Toast.makeText(EsperandoConductor.this,"Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    JSONObject obj = new JSONObject(resp);
                    json_carrera=obj;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Get_ObtenerPerfilConductor extends AsyncTask<Void, String, String> {
        private String id;

        public Get_ObtenerPerfilConductor(String id){
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
            parametros.put("evento", "get_info_con_carrera");
            parametros.put("id_carrera",id);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            try {
                if(resp == null){
                    Toast.makeText(EsperandoConductor.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                }
                if(!resp.isEmpty()) {
                    final JSONObject object = new JSONObject(resp);
                    if (object != null) {
                        final String nombreConductor = object.getString("nombre").toString();
                        final String apellido_pa = object.getString("apellido_pa").toString();
                        final String apellido_ma = object.getString("apellido_ma").toString();
                        String modelo = object.getString("modelo").toString();
                        String marca = object.getString("marca").toString();
                        int viajes = object.getInt("cant_car");

                        int amable = object.getInt("amable");
                        int buena_ruta = object.getInt("buena_ruta");
                        int auto_limpio = object.getInt("auto_limpio");
                        String ultimo_mensaje = object.getString("ultimo_mensaje");

                        String placa = object.getString("placa");
                        text_nombreConductor.setText(nombreConductor + " " + apellido_pa + " " + apellido_ma);
                        text_nombreAuto.setText(marca + "-" + modelo);
                        text_numeroPlaca.setText(placa);

                        cont_amable.setText(amable+"");
                        cont_buena_ruta.setText(buena_ruta+"");
                        cont_auto_limpio.setText(auto_limpio+"");
                        text_ultimo_mensaje.setText(ultimo_mensaje);

                        text_Viajes.setText("ha completado: " + viajes);

                        Container_verPerfil.setVisibility(View.VISIBLE);
                        btn_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(EsperandoConductor.this,Chat_Activity.class);
                                try {
                                    intent.putExtra("id_receptor",object.getString("id"));
                                    intent.putExtra("nombre_receptor",nombreConductor+" "+apellido_pa+" "+apellido_ma);
                                    intent.putExtra("id_emisor",usr_log.getString("id"));
                                    intent.putExtra("foto_perfil", object.getString("foto_perfil"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        btn_llamar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    String telefono = object.getString("telefono");

                                    number=telefono;

                                    int permissionCheck = ContextCompat.checkSelfPermission(
                                            EsperandoConductor.this, Manifest.permission.CALL_PHONE);
                                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                        Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
                                        ActivityCompat.requestPermissions(EsperandoConductor.this, new String[]{Manifest.permission.CALL_PHONE}, 225);
                                    } else {
                                        callPhone();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if(object.has("foto_perfil")){
                        if(object.getString("foto_perfil").length()>0){
                            new AsyncTaskLoadImage(img_foto).execute(getString(R.string.url_foto)+object.getString("foto_perfil"));
                        }
                    }
                }else{
                    Toast.makeText(EsperandoConductor.this,"Error al obtener Datos", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
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

    private class Cancelar_viaje extends AsyncTask<Void, String, String> {

        private String id_usr;

        private ProgressDialog progreso;

        public Cancelar_viaje() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(EsperandoConductor.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","cancelar_carrera");

            try {
                id_usr = getUsr_log().getString("id");
                param.put("id_carrera",json_carrera.getInt("id")+"");
                param.put("id_usr",id_usr);
                param.put("tipo_cancelacion",TIPO_CANCELACION+"");
                param.put("id_tipo",ID_TIPO+"");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            } catch (Exception e) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp == null){
                Toast.makeText(EsperandoConductor.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if(resp.isEmpty()) {
                Toast.makeText(EsperandoConductor.this,"Error al obtener datos", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    Json_cancelarViaje = new JSONObject(resp);
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    new Cancelar_viaje_Dialog(Json_cancelarViaje).show(fragmentManager, "Dialog");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }
    }

    public void confirmar(){
        new Confirmar_cancelacion().execute();
    }

    public class Confirmar_cancelacion extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(EsperandoConductor.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","ok_cancelar_carrera");
            param.put("json",Json_cancelarViaje.toString());
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            } catch (Exception e) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp == null){
                Toast.makeText(EsperandoConductor.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if(resp.isEmpty()){
                Toast.makeText(EsperandoConductor.this,"Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }else if(resp.contains("exito")) {
                Toast.makeText(EsperandoConductor.this,"Viaje cancelado.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EsperandoConductor.this , PedirSieteMap.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(EsperandoConductor.this,"Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }
    }

    public class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }



}
