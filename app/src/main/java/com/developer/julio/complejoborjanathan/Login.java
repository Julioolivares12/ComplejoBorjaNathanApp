package com.developer.julio.complejoborjanathan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail,txtpass;

    private Button btnIngresar;
    private Button btnLink;


    Context context=this;
   JSONArray jsonArray;
   JSONObject jsonObject;
   ProgressDialog progressDialog;
   Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = (EditText)findViewById(R.id.txtemail);
        txtpass= (EditText)findViewById(R.id.txtpass);

       // mostrarDatos();
       // txtEmail.setText(mostrarEmail());

        btnIngresar = (Button)findViewById(R.id.btnIngresar);
       btnLink = (Button)findViewById(R.id.btnLink);


        btnLink.setOnClickListener(this);

        btnIngresar.setOnClickListener(this);
    }
    public void mostrarDatos(){
        SharedPreferences sharedPreferences = getSharedPreferences("DatosAlumno",context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre","no hay datos");
        txtEmail.setText(nombre);
    }
    public String mostrarEmail(){
        String email;
        SharedPreferences sharedPreferences = getSharedPreferences("DatosAlumno",context.MODE_PRIVATE);
        email =sharedPreferences.getString("email","no hay datos");

        return email;
    }
public void guardarPreferencias(String nombre,String pass,String id,String id_tipo,String urlImagen){
        SharedPreferences sharedPreferences = getSharedPreferences("DatosAlumno",context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString("AlumnoDataNombre",nombre);
        editor.putString("AlumnoDataPass",pass);
        editor.putString("AlumnoDataId",id);
        editor.putString("AlumnoDataIdTipo",id_tipo);
        editor.putString("AlumnoDataUrlImagen",urlImagen);
        editor.commit();


        //para leer las preferencia
    /*
      *  SharedPreferences shared  = getPreferences(context.MODE_PRIVATE);
    String nombre = shared.getString("AlumnoDataNombre","no hay preferencias guardadas");
    String pass = shared.getString("AlumnoDataPass","no ha datos");
     */

}
    private void iniciaSession(String url){
        RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonObject = new JSONObject(response);
                    String pass = jsonObject.getString("pass");
                    String nombre = jsonObject.getString("nombre");
                    String id = jsonObject.getString("id_alumno");
                    String id_tipo = jsonObject.getString("id_tipo");
                    String urlImagen= jsonObject.getString("imagenUrl");
                    guardarPreferencias(nombre,pass,id,id_tipo,urlImagen);
                    Log.d("password",pass);
                  int dato = obtenerDatosJson(response);
                  Log.d("dato",String.valueOf(dato));
                  if (dato>0){
                      Intent intent = new Intent(getApplicationContext(),Home.class);
                      startActivity(intent);
                  }
                  else{
                      Toast.makeText(getApplicationContext(),"usuario o password incorrectos",Toast.LENGTH_SHORT).show();
                  }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "el usuario no existe en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnIngresar:
                String URL="http://192.168.1.10:8080/ComplejoApp/rest/login?email="+txtEmail.getText().toString()+"&pass="+txtpass.getText().toString();
                iniciaSession(URL);
                break;
            case R.id.btnLink:
                Intent irRegistro  = new Intent(getApplicationContext(),Registro.class);
                startActivity(irRegistro);
                break;
        }
    }

    // clase para trabajar en segundo plano
   public class LoginAsync extends AsyncTask<Void,Integer,Void>{


        ProgressDialog progressDialog;
        String email;
        String password;
        int progreso=0,jsondata;
        Login login;
        String resultado="";
        Intent irAHome;

        public  LoginAsync(Login login,String email,String password,ProgressDialog progressDialog){
            this.email=email;
            this.password=password;
            this.login=login;
            this.progressDialog=progressDialog;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso=0;
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            resultado= enviarDatosGet(email,password);
            Log.d("datos json",resultado);

            while (progreso<100){
                progreso++;
                publishProgress(progreso);

                SystemClock.sleep(10);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            jsondata=obtenerDatosJson(resultado);
            Log.d("verica tamaño json", String.valueOf(jsondata));
            if (jsondata>0){
                 irAHome = new Intent(getApplicationContext(),Home.class);
                irAHome.putExtra("email",email);
                startActivity(irAHome);
            }
            else{
                Toast.makeText(getApplicationContext(),"usuario o password incorrectos",Toast.LENGTH_SHORT).show();
                limpiar();
            }
            progressDialog.dismiss();
        }


    }
    //metodos que acceden a la red
    public  String enviarDatosGet(String email,String pass){
        URL url= null;
        String linea="";
        int respuesta=0;
        StringBuilder resul = null;

        try{
            url = new URL("http://192.168.1.10:8080/ComplejoAPI/validaLogin.php?email="+email+"&pass="+pass);
            //ruta con servidor: http://complejoborjaapi.webcindario.com/validaLogin.php?email=robertoolivares90@gmail.com&pass=1234
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            respuesta=connection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta==HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while ((linea=reader.readLine())!=null){
                    resul.append(linea);
                }
            }

        }
        catch (Exception ex){

        }
        return  resul.toString();
    }
    public int obtenerDatosJson(String respnse){
        int res=0;
        try{
            JSONObject json  = new JSONObject(respnse);
            if (json.length()>0){
                res=1;
            }
        }catch (Exception ex){
        }
        return res;
    }
    public void limpiar(){
        txtEmail.setText("");
        txtpass.setText("");
    }


}
