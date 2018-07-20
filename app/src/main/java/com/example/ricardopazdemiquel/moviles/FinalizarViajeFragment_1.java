package com.example.ricardopazdemiquel.moviles;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FinalizarViajeFragment_1 extends Fragment {

    private static final String TAG ="fragment_explorar";

    public FinalizarViajeFragment_1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finalizar_viaje_fragment_1, container, false);

        return view;
    }

    private void Cerrar() {
        //Usuario.setUsuario(null);
        //Intent intent = new Intent(getContext(), LoginActivity.class);
        //startActivity(intent);
        getActivity().finish();
    }

}
