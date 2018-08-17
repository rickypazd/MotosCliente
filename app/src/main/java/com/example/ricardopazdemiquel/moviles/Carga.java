package com.example.ricardopazdemiquel.moviles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import utiles.NetworkStateChangeReceiver;
import utiles.Token;

import static utiles.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class Carga extends AppCompatActivity {

    private Intent intenError;
    private boolean isfirst;
    private JSONObject usr_log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);
        Token.currentToken = FirebaseInstanceId.getInstance().getToken();
        if (isConnectedToInternet(this)) {

                Intent intent= new Intent(Carga.this,Sin_conexion.class);
                startActivity(intent);
            /////
            /// if ress{cant:8
            ///         arr:[{
            ///                 },
            ///              {
            ///                 }]
            ///             }
            ///cant == 0 sigue
            // cant >0 && menor que 11 actualiza del arr
            //camt >10  pregunta para actualizar y carga la ventana

        }
        usr_log=getUsr_log();
        ejecutar();
    }
    public void ejecutar(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (usr_log != null) {
                    try {
                        if (usr_log.getInt("id_rol") == 4) {
                            Intent intent = new Intent(Carga.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            } else {
                Intent intent = new Intent(Carga.this, Iniciar_Sesion_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }, 1000);

}
    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
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

    private boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(NetworkStateChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }
}