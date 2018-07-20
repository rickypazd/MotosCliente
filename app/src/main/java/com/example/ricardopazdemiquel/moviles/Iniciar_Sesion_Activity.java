package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Iniciar_Sesion_Activity extends AppCompatActivity  implements View.OnClickListener{

    private Button btn_crear_cuenta;
    private Button btn_iniciar_sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        btn_crear_cuenta = findViewById(R.id.btn_crear_cuenta);
        btn_iniciar_sesion = findViewById(R.id.btn_iniciar_sesion);

        btn_iniciar_sesion.setOnClickListener(this);
        btn_crear_cuenta.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_iniciar_sesion:
                intent = new Intent(Iniciar_Sesion_Activity.this, LoginCliente.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_crear_cuenta:
                intent = new Intent(Iniciar_Sesion_Activity.this, IniciarCuentaActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;


        }
    }
}
