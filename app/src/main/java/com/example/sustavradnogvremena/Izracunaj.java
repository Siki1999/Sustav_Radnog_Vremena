package com.example.sustavradnogvremena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Izracunaj extends AppCompatActivity {

    Connection con;
    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> korisnici = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izracunaj);

        TextView internet = (TextView) findViewById(R.id.internet_Izracunaj);

        String mjesec = getIntent().getStringExtra("Mjesec");

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Select * from korisnici order by korisnici.korisnickoime asc";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()){
                    korisnici.add(rs.getString(4));
                    key.add(rs.getString(1));
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, "Greška kod dohvata svih korisnika", Toast.LENGTH_LONG).show();
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) { } // ignore

                con = null;
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            internet.setVisibility(View.INVISIBLE);
            Brisi();
            Izracunaj(mjesec);
        }
        else{
            internet.setVisibility(View.VISIBLE);
        }
    }

    public void Izracunaj( String mjesec ){
        ListView list = (ListView) findViewById(R.id.listView_Izracunaj);
        String[] m = mjesec.split("-");
        String mjesecPravi = m[1] + "-" + m[0];
        ArrayList<String> li = new ArrayList<>();
        li.add("\n" + "Mjesec: " + mjesec);
        for(int i = 0; i<key.size(); i++){
            int ukupnoSati = 0;
            int ukupnoMinute = 0;
            String id = key.get(i);
            String korisnickoime = korisnici.get(i);
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                con = connectionHelper.connectionclass();
                if(con!=null){
                    String query = "Select * from radno_vrijeme where datum like '%" + mjesecPravi + "%' and korisnik_id = "
                            + id + " order by radno_vrijeme.datum desc";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()){
                        String temp = rs.getString(4);
                        String[] ras = temp.split(":");
                        if(ras[0].equals("00")){
                            ras[0] = "24";
                        }
                        String vrijemedoSati = ras[0];
                        String vrijemedoMinute = ras[1];
                        temp = rs.getString(3);
                        ras = temp.split(":");
                        String vrijemeodSati = ras[0];
                        String vrijemeodMinute = ras[1];
                        int vrijemedoSatiInt = Integer.parseInt(vrijemedoSati);
                        int vrijemedoMinuteInt = Integer.parseInt(vrijemedoMinute);
                        int vrijemeodSatiInt = Integer.parseInt(vrijemeodSati);
                        int vrijemeodMinuteInt = Integer.parseInt(vrijemeodMinute);
                        int sati = 0;
                        int minute = 0;

                        if(vrijemedoMinuteInt < vrijemeodMinuteInt){
                            sati = vrijemedoSatiInt - vrijemeodSatiInt - 1;
                            minute = 60 - vrijemeodMinuteInt + vrijemedoMinuteInt;
                        }
                        else{
                            sati = vrijemedoSatiInt - vrijemeodSatiInt;
                            minute = vrijemedoMinuteInt - vrijemeodMinuteInt;
                        }

                        ukupnoSati += sati;
                        ukupnoMinute += minute;
                        if(ukupnoMinute >= 60){
                            ukupnoSati += 1;
                            ukupnoMinute -= 60;
                        }
                    }
                }
            }catch (Exception ex){
                Toast.makeText(this, "Greška kod prikaza mjesecnog radnog vremena", Toast.LENGTH_LONG).show();
            }
            finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException sqlEx) { } // ignore

                    con = null;
                }
            }

            String vrijeme = "";
            if(ukupnoMinute == 0){
                vrijeme = String.valueOf(ukupnoSati) + " h";
            }
            else{
                vrijeme = String.valueOf(ukupnoSati) + " h " + String.valueOf(ukupnoMinute) + " min";
            }
            li.add("\n" + "Zaposlenik: " + korisnickoime + "\nVrijeme rada: " + vrijeme);
        }

        ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list2, li);
        list.setAdapter(ad);
    }

    public void Brisi(){
        YearMonth m = YearMonth.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth m1 = m.minusMonths(4);
        String mjesecZaBrisanje = m1.format(format);

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Delete from radno_vrijeme where datum like '%" + mjesecZaBrisanje + "%'";
                Statement st = con.createStatement();
                st.executeUpdate(query);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Greška kod brisanja radnog vremena", Toast.LENGTH_LONG).show();
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignored) { }

                con = null;
            }
        }
    }
}