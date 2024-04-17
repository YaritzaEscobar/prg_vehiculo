package com.prg.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

//AGREGAR PRODUCTO

public class MainActivity extends AppCompatActivity {

    CircleImageView imageCirProducto;
    private Button btnChangeImage;
    Button btnGuardar;
    TextView tempVal;
    String accion="nuevo", id="", imgproductourl="", rev="", idVehiculo="";
    FloatingActionButton btnIrvista;
    Intent tomarFotoIntent;
    utilidades utls;
    private Context _context;
    //detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cambiar color barra estado
        cambiarColorBarraEstado(getResources().getColor(R.color.grey));
        utls= new utilidades();

        //valores para los productos
        EditText txtmarca= (EditText)findViewById(R.id.txtMarca);
        EditText txtmotor= (EditText)findViewById(R.id.txtMotor);
        EditText txtchasis= (EditText)findViewById(R.id.txtChasis);
        EditText txtvin= (EditText)findViewById(R.id.txtVin);
        EditText txtcombustion= (EditText)findViewById(R.id.txtCombustion);

        imageCirProducto = findViewById(R.id.imgProductoVista); //
        btnChangeImage = findViewById(R.id.btnCambiarImagen);
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFotoProducto();

            }
        });

        //Boton para cambiar de activitys, ir a vista
        btnIrvista = findViewById(R.id.btnRegresarListaProductos);
        btnIrvista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irVista();
            }
        });

        //boton guardar producto
        btnGuardar = findViewById(R.id.btnGuardarVehiculo);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validar campo obligatorio
                if(txtmarca.getText().toString().isEmpty() || txtmotor.getText().toString().isEmpty() ||
                        txtchasis.getText().toString().isEmpty() || txtvin.getText().toString().isEmpty() ||
                        txtcombustion.getText().toString().isEmpty() ){

                    txtmarca.setError("Campo requerido");
                    txtmotor.setError("Campo requerido");
                    txtchasis.setError("Campo requerido");
                    txtvin.setError("Campo requerido");
                    txtcombustion.setError("Campo requerido");
                    return;
                } else {

                    try {
                        //VALIDAR AGREGAR PRODUCTO
                        tempVal = findViewById(R.id.txtMarca);
                        String marca = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtMotor);
                        String motor = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtChasis);
                        String chasis = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtVin);
                        String vin = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtCombustion);
                        String combustion = tempVal.getText().toString();

                        //GUARDAR DATOS SERVIDOR
                        JSONObject datosProductos = new JSONObject();
                        if (accion.equals("modificar") && id.length() > 0 && rev.length() > 0) {
                            datosProductos.put("_id", id);
                            datosProductos.put("_rev", rev);
                        }
                        datosProductos.put("idVehiculo", idVehiculo);
                        datosProductos.put("marca", marca);
                        datosProductos.put("motor", motor);
                        datosProductos.put("chasis", chasis);
                        datosProductos.put("vin", vin);
                        datosProductos.put("combustion", combustion);
                        datosProductos.put("imgproducto", imgproductourl);
                        String respuesta = "";


                        enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                        respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();

                        JSONObject respuestaJSONObject = new JSONObject(respuesta);
                        if (respuestaJSONObject.getBoolean("ok")) {
                            id = respuestaJSONObject.getString("id");
                            rev = respuestaJSONObject.getString("rev");
                        } else {
                            respuesta = "Error al guardar en servidor: " + respuesta;
                        }

                        DB db = new DB(getApplicationContext(), "", null, 1);
                        String[] datos = new String[]{id, rev, idVehiculo, marca, motor, chasis, vin, combustion, imgproductourl};
                        respuesta = db.administrar_vehiculos(accion, datos);
                        if (respuesta.equals("ok")) {
                            Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                            irVista();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + respuesta, Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        mostrarMsg("Error: " + e.getMessage());
                    }

                    //INTENCION DE SERVIDOR COUCH DB Y LOCAL- NO FUNCIONO COMPLETAMENTE
/*
                    ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    tempVal = findViewById(R.id.txtCodigo);
                    String codigo = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtNombre);
                    String nombre = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtMarca);
                    String marca = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPrecio);
                    String precio = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtDescripcion);
                    String descripcion = tempVal.getText().toString();

                    //Conexion CouchDB
                    if(networkInfo != null && networkInfo.isConnected()){
                        try {
                                //GUARDAR DATOS SERVIDOR
                                JSONObject datosProductos = new JSONObject();
                                if (accion.equals("modificar") && id.length() > 0 && rev.length() > 0) {
                                    datosProductos.put("_id", id);
                                    datosProductos.put("_rev", rev);
                                }
                                datosProductos.put("idProducto", idProducto);
                                datosProductos.put("codigo", codigo);
                                datosProductos.put("nombre", nombre);
                                datosProductos.put("marca", marca);
                                datosProductos.put("precio", precio);
                                datosProductos.put("descripcion", descripcion);
                                datosProductos.put("imgproducto", imgproductourl);
                                String respuesta = "";

                                enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                                respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();

                                JSONObject respuestaJSONObject = new JSONObject(respuesta);
                                if (respuestaJSONObject.getBoolean("ok")) {
                                    id = respuestaJSONObject.getString("id");
                                    rev = respuestaJSONObject.getString("rev");
                                } else {
                                    respuesta = "Error al guardar en servidor: " + respuesta;
                                }

                                DB db = new DB(getApplicationContext(), "", null, 1);
                                String[] datos = new String[]{id, rev, idProducto, codigo, nombre, marca, precio, descripcion, imgproductourl};
                                respuesta = db.administrar_productos(accion, datos);
                                if (respuesta.equals("ok")) {
                                    Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                                    irVista();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + respuesta, Toast.LENGTH_LONG).show();
                                }

                        } //cierre validar campo obligatorio
                        catch (Exception e) {
                            mostrarMsg("Error: " + e.getMessage());
                        }
                        //En caso de no tener conexion, guardar en local
                    } else {

                        try{

                        DB db = new DB(getApplicationContext(), "", null, 1);
                        String[] datos = new String[]{id, rev, idProducto, codigo, nombre, marca, precio, descripcion, imgproductourl};
                        String respuesta = db.administrar_productos(accion, datos);
                        if (respuesta.equals("ok")) {
                            Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                            irVista();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + respuesta, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                            mostrarMsg("Error: " + e.getMessage());
                        }

                    }  */


                } //validar campos obligatorios :3
            }
        });

        mostrarDatosProductos(); //mostrar los datos del producto

        //
    } //ONCREATE

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitmap = BitmapFactory.decodeFile(imgproductourl);
                imageCirProducto.setImageBitmap(imagenBitmap);
            }else{
                mostrarMsg("Se cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar la camara: "+ e.getMessage());
        }
    }
    private void tomarFotoProducto(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File fotoProducto = null;
        try{
            fotoProducto = crearImagenProducto();
            if( fotoProducto!=null ){
                Uri uriFotoAmigo = FileProvider.getUriForFile(MainActivity.this, "com.prg.myapplication.fileprovider", fotoProducto);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoAmigo);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("NO pude tomar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al tomar la foto: "+ e.getMessage());
        }

    }
    private File crearImagenProducto() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "imagen_"+ fechaHoraMs +"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( !dirAlmacenamiento.exists() ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        imgproductourl = image.getAbsolutePath();
        return image;
    }

    //Agarra la foto de la galeria y la convierte a Uri filepath-GALERIA-NO funciono
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageCirProducto.setImageBitmap(bitmap);

            } catch (Exception e){
                mostrarMsg("Error: "+ e.getMessage());
            }
        }
    }

    //Abrir galeria
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*"); //importante para la imagen
        intent.setAction(Intent.ACTION_GET_CONTENT); //action para tomar foto
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST); //seleccionar foto
        intent.putExtra(MediaStore.Images.Media.DATA, filePath); //aqui se debe almacenar
    }*/


    private void irVista(){ //regresar vista o lista productos
        Intent abrirVentana = new Intent(getApplicationContext(), lista_vehiculos.class);
        startActivity(abrirVentana);
    }

    private void mostrarDatosProductos(){
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if( accion.equals("modificar") ){
                JSONObject jsonObject = new JSONObject(parametros.getString("vehiculos")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idVehiculo = jsonObject.getString("idVehiculo");

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(jsonObject.getString("marca"));

                tempVal = findViewById(R.id.txtMotor);
                tempVal.setText(jsonObject.getString("motor"));

                tempVal = findViewById(R.id.txtChasis);
                tempVal.setText(jsonObject.getString("chasis"));

                tempVal = findViewById(R.id.txtVin);
                tempVal.setText(jsonObject.getString("vin"));

                tempVal = findViewById(R.id.txtCombustion);
                tempVal.setText(jsonObject.getString("combustion"));

                imgproductourl = jsonObject.getString("imgproducto");
                Bitmap bitmap = BitmapFactory.decodeFile(imgproductourl);
                imageCirProducto.setImageBitmap(bitmap);
            }else{ //nuevo registro
                idVehiculo = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+ e.getMessage());
        }
    }

    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    private void cambiarColorBarraEstado(int color) {
        // Verificar si la versión del SDK es Lollipop o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    } //fin cambiar colorbarraestado
}

