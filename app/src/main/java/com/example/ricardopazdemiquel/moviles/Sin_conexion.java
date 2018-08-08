package com.example.ricardopazdemiquel.moviles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import utiles.NetworkStateChangeReceiver;

public class Sin_conexion extends AppCompatActivity {

    private Button reintentar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_conexion);
        reintentar=findViewById(R.id.reintentar);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectedToInternet(Sin_conexion.this)) {
                    onBackPressed();
                }else{
                    Toast.makeText(Sin_conexion.this,"No Tiene Conexion a Internet.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (isConnectedToInternet(this)) {
            super.onBackPressed();
        }else{
            finish();
            moveTaskToBack(true);
            System.exit(0);
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
