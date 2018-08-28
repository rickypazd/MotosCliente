package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class Elejir_tipo_siete extends Fragment implements View.OnClickListener {

    private Button siete;
    private Button siete_maravilla;
    private Button siete_super;
    private Button siete_togo;

    double longitudeGPS, latitudeGPS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_elejir_tipo_siete, container, false);

        longitudeGPS = getActivity().getIntent().getDoubleExtra("lng",0);
        latitudeGPS = getActivity().getIntent().getDoubleExtra("lat",0);

        siete = view.findViewById(R.id.btn_siete);
        siete_maravilla = view.findViewById(R.id.btn_sieteMaravilla);
        siete_super = view.findViewById(R.id.btn_superSiete);
        siete_togo = view.findViewById(R.id.btn_togo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);

        return view;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elejir_tipo_siete);

        longitudeGPS = getIntent().getDoubleExtra("lng",0);
        latitudeGPS = getIntent().getDoubleExtra("lat",0);

        siete = findViewById(R.id.btn_siete);
        siete_maravilla = findViewById(R.id.btn_sieteMaravilla);
        siete_super = findViewById(R.id.btn_superSiete);
        siete_togo = findViewById(R.id.btn_togo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);
    }*/

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), PedirSieteMap.class);
        Intent intent_togo = new Intent(getActivity(), PedirSieteTogo.class);
        switch (view.getId()) {
            case R.id.btn_siete:
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 1);
                    startActivity(intent);
                break;
            case R.id.btn_superSiete:
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 4);
                    startActivity(intent);

                break;
            case R.id.btn_sieteMaravilla:
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 3);
                    startActivity(intent);

                break;
            case R.id.btn_togo:
                    intent_togo.putExtra("lng", longitudeGPS);
                    intent_togo.putExtra("lat", latitudeGPS);
                    intent_togo.putExtra("tipo", 2);
                    startActivity(intent_togo);
                break;
        }
    }
}