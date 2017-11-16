package com.developer.julio.complejoborjanathan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    EditText txtnombre,txtemail,txtpass,txtapellido;
    Button btnRegistro,btnCancelar;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtnombre = (EditText)findViewById(R.id.campo_nombre);
        txtapellido = (EditText)findViewById(R.id.txtapellido);
        txtemail= (EditText)findViewById(R.id.txtemail);

        txtpass = (EditText)findViewById(R.id.txtpass);


        btnRegistro = (Button)findViewById(R.id.btnRegistrar);
        btnCancelar=(Button)findViewById(R.id.btnCancelar);


        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url= "http://192.168.1.10:8080/ComplejoApp/rest/registrarUsuario";
                registrar(url);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent salir = new Intent(getApplicationContext(),Login.class);
                startActivity(salir);
            }
        });
    }
    private void registrar(String url){
        RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(Registro.this, "Register OK", Toast.LENGTH_LONG).show();
                    openLogin();
                }else{
                    Toast.makeText(Registro.this,response,Toast.LENGTH_LONG).show();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new  HashMap<String,String>();
                params.put("nombre",txtnombre.getText().toString());
                params.put("apellido",txtapellido.getText().toString());
                params.put("email",txtemail.getText().toString());
                params.put("pass",txtpass.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void openLogin(){
        Intent irLogin = new Intent(getApplicationContext(),Login.class);
        startActivity(irLogin);
    }
}

