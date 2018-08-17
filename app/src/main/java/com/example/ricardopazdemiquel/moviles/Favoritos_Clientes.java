package com.example.ricardopazdemiquel.moviles;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ricardopazdemiquel.moviles.Fragment.List_historial_fragment;

public class Favoritos_Clientes extends AppCompatActivity implements View.OnClickListener {

    private Button btn_agregar_ubicacion;
    Fragment fragment_historial  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_clientes);

        btn_agregar_ubicacion = findViewById(R.id.btn_agregar_ubicacion);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment_historial = new List_historial_fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_historial).commit();

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
            case R.id.btn_agregar_ubicacion:

                break;
        }
    }
}
