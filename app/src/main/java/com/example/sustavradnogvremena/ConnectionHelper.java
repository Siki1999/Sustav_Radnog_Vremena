package com.example.sustavradnogvremena;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String uname, pass, ip, port, database;

    @SuppressLint("NewAPI")
    public Connection connectionclass(){
        ip = "remotemysql.com";
        database = "aWMxLHPmcP";
        uname = "aWMxLHPmcP";
        pass = "GAsZwfgc6B";
        port = "3306";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://"+ ip + ":" + port + "/" + database, uname, pass);
        }
        catch (Exception ex){
            Log.e("Error",ex.toString());
        }

        return con;
    }
}
