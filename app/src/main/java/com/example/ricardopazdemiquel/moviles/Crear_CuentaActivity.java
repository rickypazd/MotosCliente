package com.example.ricardopazdemiquel.moviles;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;


public class Crear_CuentaActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText edit_fecha;
    private EditText edit_username;
    private EditText edit_pass;
    private EditText edit_correo;
    private Button btn_crear_usuario;
    private JSONObject obj_us;
    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        edit_fecha = findViewById(R.id.edit_fecha);
        edit_username= findViewById(R.id.edit_username);
        edit_pass = findViewById(R.id.edit_pass);
        edit_correo = findViewById(R.id.edit_correo);
        btn_crear_usuario = findViewById(R.id.btn_crear_usuario);

        btn_crear_usuario.setOnClickListener(this);
        edit_fecha.setOnClickListener(this);

        String d = getIntent().getStringExtra("obj_cliente");
        if(d.length()>0){
            try {
                obj_us = new JSONObject(d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                int solicitud_id = (int)id;
                createSimpleDialog(solicitud_id).show();
            }
        });*/
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
            case R.id.btn_crear_usuario:
                Guardar();
                break;
        }
        switch (view.getId()) {
            case R.id.edit_fecha:
                ShowDatapinckerDialog();
                break;
        }
    }

    private void ShowDatapinckerDialog(){
        final java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(Calendar.YEAR , 1900);
        c.set(Calendar.MONTH , 0);
        c.set(Calendar.DAY_OF_MONTH , 0);
        int año = c.get(java.util.Calendar.YEAR);
        int mes = c.get(java.util.Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog
                (this ,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha;
                        if ((month+1)<10) {
                            fecha = year + "-0" + (month+1);
                        }else{
                            fecha = year + "-" + (month+1);
                        }
                        if(dayOfMonth<10){
                            fecha += "-0"+ dayOfMonth;
                        }else{
                            fecha += "-" + dayOfMonth;
                        }
                        edit_fecha.setText(fecha);
                        edit_fecha.setError(null);
                    }
                }, dia, mes , año);
        datePickerDialog.show();
    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean validarEmailSimple(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    private void Guardar() {

        String fechaV = edit_fecha.getText().toString().trim();
        String usernameV = edit_username.getText().toString().trim();
        String passV = edit_pass.getText().toString().trim();
        String correoV = edit_correo.getText().toString().trim();

        boolean isValid = true;

        if (fechaV.isEmpty()) {
            edit_fecha.setError("Campo obligatorio");
            isValid = false;
        }
        if (usernameV.isEmpty()) {
            edit_username.setError("Campo obligatorio");
            isValid = false;
        }
        if (passV.isEmpty()) {
            edit_pass.setError("campo obligarotio");
            isValid = false;
        } else if (isPasswordValid(passV) == false) {
            edit_pass.setError("la contraseña debe ser mayor a 4 digitos");
            isValid = false;
        }
        if (correoV.isEmpty()) {
            edit_correo.setError("campo obligarotio");
            isValid = false;
        }else if (validarEmailSimple(correoV) == false) {
            edit_correo.setError("email no valido");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        String passmd5 = md5(passV);
        new Registrar(fechaV,usernameV,passmd5,correoV).execute();
    }


    private class Registrar extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private String  fecha ,  usuario ,  pass , correo;
        public Registrar( String fecha ,String usuario , String pass
                , String correo ) {
            this.fecha = fecha;
            this.usuario = usuario;
            this.pass = pass;
            this.correo = correo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Crear_CuentaActivity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if(obj_us!=null){
                try {
                    Hashtable<String,String> param = new Hashtable<>();
                    param.put("evento","registrar_usuario_cliente");
                    param.put("nombre",obj_us.getString("nombre"));
                    param.put("fecha",fecha);
                    param.put("apellido_pa",obj_us.getString("apellido_pa"));
                    param.put("apellido_ma", obj_us.getString("apellido_ma"));
                    param.put("usuario",usuario);
                    param.put("pass", pass);
                    param.put("sexo",obj_us.getString("sexo"));
                    param.put("correo",correo);
                    param.put("telefono",obj_us.getString("telefono"));
                    String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
                    return respuesta;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return "";
        }
        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);
            progreso.dismiss();
            if(pacientes==null){
                Toast.makeText(Crear_CuentaActivity.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
            }else{
                if (pacientes.equals("falso")) {
                    return;
                }else{
                    Intent inte = new Intent(Crear_CuentaActivity.this,LoginCliente.class);
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

