package com.prg.myapplication;

import java.util.Base64;

public class utilidades {
    static String urlConsulta = "http://192.168.83.29:5984/vehiculos/_design/vehiculos/_view/vehiculos";

    static String urlMto = "http://192.168.83.29:5984/vehiculos";
    static String user = "admin";
    static String passwd = "yarje";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}