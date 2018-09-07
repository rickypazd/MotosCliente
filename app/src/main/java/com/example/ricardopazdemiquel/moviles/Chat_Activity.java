package com.example.ricardopazdemiquel.moviles;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Adapter.Adapter_chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;


public class Chat_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_enviar;
    private TextView tv_nombre_receptor;
    private TextView text_mensaje;
    private ListView lv;
    private JSONArray chats;
    private Adapter_chat adapter_chat;
    private int id_emisor;
    private int id_receptor;
    private String nombre_receptor;
    private com.mikhaellopez.circularimageview.CircularImageView foto_img;
    private BroadcastReceiver broadcastReceiverMessage;

    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_list_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        //nuevo comentario
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        tv_nombre_receptor=findViewById(R.id.nombre_receptor);
        btn_enviar = findViewById(R.id.btn_enviar);
        foto_img = findViewById(R.id.foto_img);
        text_mensaje = findViewById(R.id.text_mensaje);
        lv = findViewById(R.id.list_chat);
        id_emisor=Integer.parseInt(getIntent().getStringExtra("id_emisor"));
        id_receptor=Integer.parseInt(getIntent().getStringExtra("id_receptor"));
        nombre_receptor=getIntent().getStringExtra("nombre_receptor");
        nombre_receptor=getIntent().getStringExtra("nombre_receptor");
        String img = getIntent().getStringExtra("foto_perfil");
        if(img.length()>0){
            new AsyncTaskLoadImage(foto_img).execute(getString(R.string.url_foto)+img);
        }
        tv_nombre_receptor.setText(nombre_receptor);
        btn_enviar.setOnClickListener(this);

        // creo el chat
        chats=getChat();
        if(chats==null){
            chats=new JSONArray();
        }
        adapter_chat = new Adapter_chat(this,chats,id_emisor);
        lv.setAdapter(adapter_chat);
        lv.setSelection(chats.length());

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


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiverMessage == null){
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    notificacionReciber(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessage,new IntentFilter("nuevo_mensaje"));
    }

    private void notificacionReciber(Intent intent){
        chats=getChat();
        if(chats==null){
            chats=new JSONArray();
        }
        adapter_chat = new Adapter_chat(this,chats,id_emisor);
        lv.setAdapter(adapter_chat);
        lv.setSelection(chats.length());
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
        switch (view.getId()) {
            case R.id.btn_enviar:
                enviar_mensaje();
                break;
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
    public void setMensaje(JSONObject mensaje){
        JSONArray mensajes= getChat();
        if(mensajes==null){
            mensajes=new JSONArray();
        }
        mensajes.put(mensaje);
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("chat_carrera", mensajes.toString());
        editor.commit();

    }
    public JSONArray getChat() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("chat_carrera", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONArray chat = new JSONArray(usr);
                return chat;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void enviar_mensaje() {

        String mensaje = text_mensaje.getText().toString().trim();

        boolean isValid = true;
        if (mensaje.isEmpty()) {
            text_mensaje.setError("campo obligarotio");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("mensaje",mensaje);
            obj.put("id_emisor",id_emisor);
            obj.put("id_receptor",id_receptor);
            chats.put(obj);
            adapter_chat.notifyDataSetChanged();
            lv.setSelection(chats.length());
            text_mensaje.setText("");
            setMensaje(obj);
            new enviarMensaje(id_emisor,id_receptor,mensaje).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new Registrar(fechaV,usernameV,passmd5,correoV).execute();

    }

    private class enviarMensaje extends AsyncTask<Void, String, String> {

        private int id_emisor;
        private int id_receptor;
        private String mensaje;

        public enviarMensaje(int id_emisor, int id_receptor, String mensaje) {
            this.id_emisor = id_emisor;
            this.id_receptor = id_receptor;
            this.mensaje = mensaje;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","enviar_mensaje");
            param.put("id_emisor",id_emisor+"");
            param.put("id_receptor",id_receptor+"");
            param.put("mensaje",mensaje+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null || resp.isEmpty()) {
                Toast.makeText(Chat_Activity.this,"Error al optener Datos",
                        Toast.LENGTH_SHORT).show();
            }else{

            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
}

