package sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by julio on 15/11/2017.
 */

public class Preferences extends AppCompatActivity {
    String nombre,mensaje="no hay datos";
    public String obtenerNombre(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences("DatosAlumno",context.MODE_PRIVATE);
        nombre=sharedPreferences.getString("nombre",mensaje);
        return nombre;
    }
}
