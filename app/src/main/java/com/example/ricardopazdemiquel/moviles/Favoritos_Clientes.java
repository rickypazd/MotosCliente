package com.example.ricardopazdemiquel.moviles;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ricardopazdemiquel.moviles.Fragment.List_historial_fragment;

public class Favoritos_Clientes extends AppCompatActivity implements View.OnClickListener {

    private Button btn_elegir_destino;
    private LinearLayout container_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_clientes);

        container_frame = findViewById(R.id.container_frame);
        btn_elegir_destino = findViewById(R.id.btn_elegir_destino);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            case R.id.btn_elegir_destino:
                container_frame.setVisibility(View.GONE);
                break;
        }
    }

}
