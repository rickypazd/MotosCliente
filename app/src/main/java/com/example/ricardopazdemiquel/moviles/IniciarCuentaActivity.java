package com.example.ricardopazdemiquel.moviles;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class IniciarCuentaActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText edit_nombre;
    private EditText edit_apellidoP;
    private EditText edit_apellidoM;
    private EditText edit_telefono;

    private RadioButton radio_hombre;
    private RadioButton radio_mujer;

    private Button btn_siguiente;
    private String sexo;

    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_solicitud);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_nombre = findViewById(R.id.edit_nombre);
        edit_apellidoP= findViewById(R.id.edit_apellidoP);
        edit_apellidoM = findViewById(R.id.edit_apellidoM);
        edit_telefono = findViewById(R.id.edit_telefono);
        radio_hombre = findViewById(R.id.radioHombre);
        radio_mujer = findViewById(R.id.radioMujer);
        btn_siguiente = findViewById(R.id.btn_siguiente);

        btn_siguiente.setOnClickListener(this);

        Radio();

    }

    private void Radio() {
        radio_hombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio_hombre.isChecked() == true) {
                    setSexo("Hombre");
                    //radioButton_trabajar.setError(null);
                    //radioButton_trabajar.setChecked(false);
                }
            }
        });
        radio_mujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio_mujer.isChecked() == true) {
                    setSexo("Mujer");
                    //radioButton_contrato.setError(null);
                    //radioButton_contrato.setChecked(false);
                }
            }
        });
    }

    /*public AlertDialog createSimpleDialog(final int id_solicitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Solicitud")
                .setMessage("Confirmar el freelancer")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AceptarFrelancer(id_solicitud);
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }*/


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
            case R.id.btn_siguiente:
                Guardar();
                break;
        }
    }


    private void Guardar() {
        String nombreV = edit_nombre.getText().toString().trim();
        String apellidoPV = edit_apellidoP.getText().toString().trim();
        String apellidoMV = edit_apellidoM.getText().toString().trim();
        String telefonoV = edit_telefono.getText().toString().trim();

        boolean isValid = true;

        if (nombreV.isEmpty()) {
            edit_nombre.setError("debe ingresar su nombre");
            isValid = false;
        }
        if (apellidoPV.isEmpty()) {
            edit_apellidoP.setError("Campo Obligatorio");
            isValid = false;
        }
        if (telefonoV.isEmpty()) {
            edit_telefono.setError("campo obligarotio");
            isValid = false;
        }
        if (!isValid) {
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            Intent intent = new Intent(this,Crear_CuentaActivity.class);
            obj.put("nombre",nombreV);
            obj.put("apellido_pa",apellidoPV);
            obj.put("apellido_ma",apellidoMV);
            obj.put("telefono",telefonoV);
            obj.put("sexo",getSexo());
            intent.putExtra("obj_cliente", obj.toString());
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

