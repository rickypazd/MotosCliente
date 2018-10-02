package com.example.ricardopazdemiquel.moviles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.signin.*;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.Token;
import utils.Tools;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginSocial extends AppCompatActivity {

    //login face
    private static final String TAG = "LoginSocial";
    private static final int RC_SIGN_IN = 9001;
    private LoginButton loginButton;
    private Button login;
    private CallbackManager callbackManager;
    private static final int MAX_STEP = 5;
    private SignInButton signInButton;
    private ViewPager viewPager;
    private Button btnNext;
    private GoogleSignInClient mGoogleSignInClient;
    private MyViewPagerAdapter myViewPagerAdapter;
    private String about_title_array[] = {
            "iMotos",
            "iMotos",
            "iMotos",
            "iMotos"
    };
    private String about_description_array[] = {
            "Facilita tus compras",
            "24 horas.",
            "Ahorra Tiempo",
            "Mas confiable",
    };
    private int about_images_array[] = {
            R.drawable.ic_icon_imoto,
            R.drawable.ic_icon_perfil_moto,
            R.drawable.ic_icon_preferencia_moto,
            R.drawable.ic_icon_viajes_moto
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_social);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        InitLoginFacebook();
        // adding bottom dots
        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter(loginButton);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin_overlap));
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager.getCurrentItem() == about_title_array.length ) {
                    btnNext.setVisibility(View.GONE);

                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText("Siguiente");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < MAX_STEP) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    //finish();
                }
            }
        });

        Tools.setSystemBarColor(this, R.color.grey_10);
        Tools.setSystemBarLight(this);
        // Set the dimensions of the sign-in button.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                      //  updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }


    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this,account.getDisplayName(),Toast.LENGTH_LONG).show();
            String id=account.getId();
            try {
                String resp= new get_usr_gmail(id).execute().get();
                if(resp==null){
                    Toast.makeText(LoginSocial.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }else{
                    if (resp.contains("falso")) {
                        Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    } else {
                        try {
                            JSONObject obj = new JSONObject(resp);
                            if(obj.getString("exito").equals("si")) {
                                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("usr_log", obj.toString());
                                editor.commit();
                                Intent intent = new Intent(LoginSocial.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{

                                ////esteeee <=-------
                                Intent intent = new Intent(LoginSocial.this,Iniciar_cuenta_gmail_Activity.class);
                                JSONObject object = new JSONObject();
                                object.put("diplayname",account.getDisplayName());
                                object.put("email",account.getEmail());
                                object.put("id",account.getId());
                                object.put("familyname",account.getFamilyName());
                                object.put("givenname",account.getGivenName());
                                intent.putExtra("usr_face",object.toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //LoginManager.getInstance().logOut();
                                signOut();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
    private void InitLoginFacebook(){
        callbackManager = CallbackManager.Factory.create();

        loginButton= findViewById(R.id.loginFacebook);
        loginButton.setReadPermissions("email");


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String id=object.getString("id");
                                        try {
                                        String resp= new get_usr_face(id).execute().get();
                                        if(resp==null){
                                            Toast.makeText(LoginSocial.this,"Error al conectarse con el servidor.",Toast.LENGTH_SHORT).show();
                                            LoginManager.getInstance().logOut();
                                        }else{
                                            if (resp.contains("falso")) {
                                                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                                            } else {
                                                try {
                                                    JSONObject obj = new JSONObject(resp);
                                                    if(obj.getString("exito").equals("si")) {
                                                        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = preferencias.edit();
                                                        editor.putString("usr_log", obj.toString());
                                                        editor.commit();
                                                        Intent intent = new Intent(LoginSocial.this,MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }else{

                                                        ////esteeee <=-------
                                                        Intent intent = new Intent(LoginSocial.this,Iniciar_cuenta_fb_Activity.class);
                                                        intent.putExtra("usr_face",object.toString());
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        LoginManager.getInstance().logOut();

                                                    }


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,middle_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(LoginSocial.this, Arrays.asList("public_profile,email"));

                    }
                }).executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logInWithReadPermissions(LoginSocial.this, Arrays.asList("public_profile,email,user_birthday"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
    private LoginButton loginBFace;
        public MyViewPagerAdapter(LoginButton loginBFace) {
            this.loginBFace=loginBFace;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if(position==MAX_STEP-1){
                view = layoutInflater.inflate(R.layout.item_login, container, false);
                ((Button) view.findViewById(R.id.logestandar)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginSocial.this,Iniciar_Sesion_Activity.class);
                        startActivity(intent);
                    }
                });
                ((Button) view.findViewById(R.id.btnface)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginBFace.callOnClick();
                    }
                });
                ((Button) view.findViewById(R.id.button8)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });
            }else{
                 view = layoutInflater.inflate(R.layout.item_card_wizard, container, false);
                ((TextView) view.findViewById(R.id.title)).setText(about_title_array[position]);
                ((TextView) view.findViewById(R.id.description)).setText(about_description_array[position]);
                ((ImageView) view.findViewById(R.id.image)).setImageResource(about_images_array[position]);


            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return about_title_array.length+1;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public class get_usr_face extends AsyncTask<Void, String, String> {
        private String id;
        public get_usr_face(String id){
            this.id=id;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario_face");
            parametros.put("id_usr",id+"");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);


        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
    public class get_usr_gmail extends AsyncTask<Void, String, String> {
        private String id;
        public get_usr_gmail(String id){
            this.id=id;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario_gmail");
            parametros.put("id_usr",id+"");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);


        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
}
