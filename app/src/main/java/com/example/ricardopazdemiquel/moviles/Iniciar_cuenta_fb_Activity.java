package com.example.ricardopazdemiquel.moviles;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;


public class Iniciar_cuenta_fb_Activity extends AppCompatActivity implements View.OnClickListener{


    private EditText edit_nombre;
    private EditText edit_apellidoP;
    private EditText edit_telefono;
    private EditText edit_correo;
    private com.mikhaellopez.circularimageview.CircularImageView img_photo;

    private RadioButton radio_hombre;
    private RadioButton radio_mujer;
    private Button btn_siguiente;
    private String sexo;
    private String id;

    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_iniciar_cuenta_fb);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_nombre = findViewById(R.id.edit_nombre);
        edit_apellidoP= findViewById(R.id.edit_apellidoP);
        edit_telefono = findViewById(R.id.edit_telefono);
        radio_hombre = findViewById(R.id.radioHombre);

        radio_mujer = findViewById(R.id.radioMujer);
        edit_correo = findViewById(R.id.edit_correo);
        img_photo= findViewById(R.id.img_photo);
        btn_siguiente = findViewById(R.id.btn_siguiente);

        btn_siguiente.setOnClickListener(this);

        Radio();

        Intent it = getIntent();
        if (it != null) {
            try {
                JSONObject obj = new JSONObject(it.getStringExtra("usr_face"));
                if(obj.has("id")){
                    id = obj.getString("id");
                }
                String nombre1="";
                String nombre2="";
                if(obj.has("first_name")){
                     nombre1 = obj.getString("first_name");
                }
                if(obj.has("middle_mane")){
                     nombre2 = obj.getString("middle_mane");
                }
                edit_nombre.setText(nombre1+" "+nombre2);
                if(obj.has("last_name")){
                    edit_apellidoP.setText(obj.getString("last_name"));
                }
                if(obj.has("email")){
                    edit_correo.setText(obj.getString("email"));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void Radio() {
        radio_hombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio_hombre.isChecked() == true) {
                    setSexo("Hombre");
                    radio_mujer.setChecked(false);
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
                    radio_hombre.setChecked(false);
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

    public static boolean validarEmailSimple(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void Guardar() {
        String nombreV = edit_nombre.getText().toString().trim();
        String apellidoV = edit_apellidoP.getText().toString().trim();
        String correoV = edit_correo.getText().toString().trim();
        String telefonoV = edit_telefono.getText().toString().trim();

        boolean isValid = true;

        if (nombreV.isEmpty()) {
            edit_nombre.setError("campo obligatorio");
            isValid = false;
        }
        if (apellidoV.isEmpty()) {
            edit_apellidoP.setError("campo obligatorio");
            isValid = false;
        }
        if (correoV.isEmpty()) {
            edit_correo.setError("campo obligatorio");
            isValid = false;
        }else if (validarEmailSimple(correoV) == false) {
            edit_correo.setError("email no valido");
            isValid = false;
        }if(getSexo().isEmpty()){
            Toast.makeText(Iniciar_cuenta_fb_Activity.this,"Elija su sexo.",Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        new Registrar(id , nombreV , apellidoV , correoV ,telefonoV , getSexo()).execute();
    }

    private class Registrar extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        private String  id , nombre ,  apellidos, correo , telefono ,  sexo ;
        public Registrar(String id ,String nombre ,String apellidos , String correo , String telefono , String sexo) {
            this.id= id;
            this.nombre= nombre;
            this.apellidos = apellidos;
            this.correo = correo;
            this.telefono = telefono;
            this.sexo = sexo;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Iniciar_cuenta_fb_Activity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","registrar_usuario_face");
            param.put("id",id);
            param.put("nombre",nombre);
            param.put("apellidos",apellidos);
            param.put("correo",correo);
            param.put("telefono", telefono);
            param.put("sexo",sexo);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String Cliente) {
            super.onPostExecute(Cliente);
            progreso.dismiss();
            if(Cliente==null){
                Toast.makeText(Iniciar_cuenta_fb_Activity.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
            }else{
                if (Cliente.equals("falso")) {
                    return;
                }else{
                    Intent inte = new Intent(Iniciar_cuenta_fb_Activity.this,MainActivity.class);
                    inte.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(inte);
                    finish();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }



}

