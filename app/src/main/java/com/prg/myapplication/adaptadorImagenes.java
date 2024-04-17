package com.prg.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<vehiculos> datosVehiculosArrayList;
    vehiculos misVehiculos;
    LayoutInflater layoutInflater;

    public adaptadorImagenes(Context context, ArrayList<vehiculos> datosVehiculosArrayList) {
        this.context = context;
        this.datosVehiculosArrayList = datosVehiculosArrayList;
    }
    @Override
    public int getCount() {
        return datosVehiculosArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return datosVehiculosArrayList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;//Long.parseLong(datosAmigosArrayList.get(i).getIdAmigo());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        try{
            misVehiculos = datosVehiculosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblMarca);
            tempVal.setText(misVehiculos.getMarca());

            tempVal = itemView.findViewById(R.id.lblMotor);
            tempVal.setText(misVehiculos.getMotor());

            tempVal = itemView.findViewById(R.id.lblChasis);
            tempVal.setText(misVehiculos.getChasis());

            tempVal = itemView.findViewById(R.id.lblVin);
            tempVal.setText(misVehiculos.getVin());

            tempVal = itemView.findViewById(R.id.lblCombustion);
            tempVal.setText(misVehiculos.getCombustion());

            ImageView imgView = itemView.findViewById(R.id.imgProductoListVista);
            Bitmap bitmap = BitmapFactory.decodeFile(misVehiculos.getImgproducto());
            imgView.setImageBitmap(bitmap);
        }catch (Exception e){
            Toast.makeText(context, "Error al mostrar datos: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}