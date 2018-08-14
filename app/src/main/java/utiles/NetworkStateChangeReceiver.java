package utiles;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.ricardopazdemiquel.moviles.Carga;
import com.example.ricardopazdemiquel.moviles.Sin_conexion;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkStateChangeReceiver extends BroadcastReceiver {
    public static final String NETWORK_AVAILABLE_ACTION = "com.ajit.singh.NetworkAvailable";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";
    private Intent intent;
    @Override
    public void onReceive(Context context, Intent intent) {
        String con = intent.getAction();
        if(!isConnectedToInternet(context)){
            intent= new Intent(context,Sin_conexion.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            if(intent!=null){

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