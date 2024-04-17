package com.prg.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname = "db_vehiculos";
    private static final int v=1;
    private static final String SQldb = "CREATE TABLE vehiculos(id text, rev text, idVehiculo text, marca text, motor text, chasis text, vin text, combustion text, imgproducto text)";
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQldb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //para hacer la actualizacion de la BD
    }
    public String administrar_vehiculos(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            if(accion.equals("nuevo")){
                db.execSQL("INSERT INTO vehiculos(id,rev,idVehiculo,marca,motor,chasis,vin,combustion,imgproducto) VALUES('"+ datos[0] +"', '"+ datos[1] +"', '"+
                        datos[2] +"', '"+ datos[3] +"','"+ datos[4] +"','"+ datos[5] +"','"+ datos[6] +"','"+ datos[7] +"', '"+ datos[8] +"')");
            } else if (accion.equals("modificar")) {
                db.execSQL("UPDATE vehiculos SET id='"+ datos[0] +"',rev='"+ datos[1] +"', marca='"+ datos[3] +"',motor='"+ datos[4] +"', chasis='"+
                        datos[5] +"', vin='"+ datos[6] +"', combustion='"+ datos[7] +"', imgproducto='"+ datos[8] +"' WHERE idVehiculo='"+ datos[2] +"'");
            } else if (accion.equals("eliminar")) {
                db.execSQL("DELETE FROM vehiculos WHERE idVehiculo='"+ datos[2] +"'");
            }
            return "ok";
        }catch (Exception e){
            return "Error: "+ e.getMessage();
        }
    }
    public Cursor consultar_vehiculos(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM vehiculos ORDER BY marca", null);
        return cursor;

    }
}
