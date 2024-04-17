package com.prg.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class lista_vehiculos extends AppCompatActivity {
    Bundle parametros = new Bundle();
    FloatingActionButton btn;
    ListView lts;
    Cursor cVehiculos;
    DB dbVehiculos;
    vehiculos misVehiculos;
    final ArrayList<vehiculos> alVehiculos=new ArrayList<vehiculos>();
    final ArrayList<vehiculos> alVehiculosCopy=new ArrayList<vehiculos>();
    JSONArray datosJSON;
    JSONObject jsonObject;
    obtenerDatosServidor datosServidor;
    detectarInternet di;
    int posicion=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_vehiculos);

        btn = findViewById(R.id.btnAbrirNuevosVehiculos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            }
        });
        try{
            di = new detectarInternet(getApplicationContext());
            if(di.hayConexionInternet()){
                obtenerDatosVehiculosServidor();
            }else{
                obtenervehiculos();//offline
            }
        }catch (Exception e){
            mostrarMsg("Error al detectar si hay conexion "+ e.getMessage());
        }
        buscarvehiculos();
    }
    private void obtenerDatosVehiculosServidor(){
        try{
            datosServidor = new obtenerDatosServidor();
            String data = datosServidor.execute().get();
            jsonObject = new JSONObject(data);
            datosJSON = jsonObject.getJSONArray("rows");
            mostrarDatosVehiculos();
        }catch (Exception e){
            mostrarMsg("Error al obtener datos desde el servidor: "+ e.getMessage());
        }
    }
    private void mostrarDatosVehiculos(){
        try{
            if( datosJSON.length()>0 ){
                lts = findViewById(R.id.ltsVeiculos);

                alVehiculos.clear();
                alVehiculosCopy.clear();

                JSONObject misDatosJSONObject;
                for (int i=0; i<datosJSON.length(); i++){
                    misDatosJSONObject = datosJSON.getJSONObject(i).getJSONObject("value");
                    misVehiculos = new vehiculos(
                            misDatosJSONObject.getString("_id"),
                            misDatosJSONObject.getString("_rev"),
                            misDatosJSONObject.getString("idVehiculo"),
                            misDatosJSONObject.getString("marca"),
                            misDatosJSONObject.getString("motor"),
                            misDatosJSONObject.getString("chasis"),
                            misDatosJSONObject.getString("vin"),
                            misDatosJSONObject.getString("combustion"),
                            misDatosJSONObject.getString("imgproducto")
                    );
                    alVehiculos.add(misVehiculos);
                }
                adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alVehiculos);
                lts.setAdapter(adImagenes);
                alVehiculosCopy.addAll(alVehiculos);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay datos que mostrar.");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+e.getMessage());
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("marca"));
        }catch (Exception e){
            mostrarMsg("Error al mostrar el menu: "+ e.getMessage());
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()){
                case R.id.mnxAgregar:
                    parametros.putString("accion", "nuevo");
                    abrirActividad(parametros);
                    break;
                case R.id.mnxModificar:
                    parametros.putString("accion","modificar");
                    parametros.putString("vehiculos", datosJSON.getJSONObject(posicion).toString());
                    abrirActividad(parametros);
                    break;
                case R.id.mnxEliminar:
                    eliminarVehiculos();
                    break;
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error en menu: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarVehiculos(){
        try {
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(lista_vehiculos.this);
            confirmacion.setTitle("Esta seguro de Eliminar a: ");
            confirmacion.setMessage(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("marca"));
            confirmacion.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = dbVehiculos.administrar_vehiculos("eliminar", new String[]{cVehiculos.getString(0)});
                    if (respuesta.equals("ok")) {
                        mostrarMsg("Vehiculo eliminado con exito.");
                        obtenervehiculos();
                    } else {
                        mostrarMsg("Error al eliminar vehiculo: " + respuesta);
                    }
                }
            });
            confirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error al eliminar: "+ e.getMessage());
        }
    }
    private void abrirActividad(Bundle parametros){
        Intent abriVentana = new Intent(getApplicationContext(), MainActivity.class);
        abriVentana.putExtras(parametros);
        startActivity(abriVentana);
    }
    private void obtenervehiculos(){
        try{
            dbVehiculos = new DB(lista_vehiculos.this, "", null, 1);
            cVehiculos = dbVehiculos.consultar_vehiculos();
            if ( cVehiculos.moveToFirst() ){
                datosJSON = new JSONArray();
                do{
                    jsonObject = new JSONObject();
                    JSONObject jsonObjectValue = new JSONObject();
                    jsonObject.put("_id",cVehiculos.getString(0));
                    jsonObject.put("_rev",cVehiculos.getString(1));
                    jsonObject.put("idVehiculo",cVehiculos.getString(2));
                    jsonObject.put("marca",cVehiculos.getString(3));
                    jsonObject.put("motor",cVehiculos.getString(4));
                    jsonObject.put("chasis",cVehiculos.getString(5));
                    jsonObject.put("vin",cVehiculos.getString(6));
                    jsonObject.put("combustion",cVehiculos.getString(7));
                    jsonObject.put("imgproducto",cVehiculos.getString(8));

                    jsonObjectValue.put("value", jsonObject);
                    datosJSON.put(jsonObjectValue);
                }while(cVehiculos.moveToNext());
                mostrarDatosVehiculos();
            }else{
                mostrarMsg("No hay vehiculos que mostrar");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener vehiculos: "+ e.getMessage());
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void buscarvehiculos(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarvehiculos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    alVehiculos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if( valor.length()<=0 ){
                        alVehiculos.addAll(alVehiculosCopy);
                    }else{
                        for(vehiculos  vehiculo: alVehiculosCopy){
                            String marca = vehiculo.getMarca();
                            String motor = vehiculo.getMotor();
                            String chasis = vehiculo.getChasis();
                            String vin = vehiculo.getVin();
                            String combustion = vehiculo.getCombustion();
                            if( marca.toLowerCase().trim().contains(valor) ||
                                    motor.toLowerCase().trim().contains(valor) ||
                                    chasis.trim().contains(valor) ||
                                    vin.trim().toLowerCase().contains(valor) ||
                                    combustion.trim().toLowerCase().contains(valor)){
                                alVehiculos.add(vehiculo);
                            }
                        }
                        adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alVehiculos);
                        lts.setAdapter(adImagenes);
                    }
                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+e.getMessage() );
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

