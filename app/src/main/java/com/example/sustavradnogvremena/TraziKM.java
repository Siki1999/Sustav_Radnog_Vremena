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
import java.util.ArrayList;
import java.util.Date;

public class TraziKM extends AppCompatActivity {

    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trazi_km);

        String korisnik = getIntent().getStringExtra("Korisnik");
        String mjesec = getIntent().getStringExtra("Mjesec");

        TextView internet = (TextView) findViewById(R.id.internet_TraziKM);

        String korisnickoime = "";
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Select * from korisnici where id =" + korisnik;
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()){
                    korisnickoime = rs.getString(4);
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, "Greška kod dohvata korisnickog imena", Toast.LENGTH_LONG).show();
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
            Trazi(korisnik, mjesec, korisnickoime);
        }
        else{
            internet.setVisibility(View.VISIBLE);
        }
    }

    public void Trazi(String korisnik, String mjesec, String korisnickoime){
        ListView list = (ListView) findViewById(R.id.listView_KM);
        ArrayList<String> li = new ArrayList<>();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null) {
                String[] m = mjesec.split("-");
                String mjesecPravi = m[1] + "-" + m[0];
                String query = "Select * from radno_vrijeme where datum like '%" + mjesecPravi + "%' and korisnik_id = "
                        + korisnik + " order by radno_vrijeme.datum desc";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()){
                    String temp = rs.getString(2);
                    String[] ras = temp.split("-");
                    String datumPravi = ras[2] + "-" + ras[1] + "-" + ras[0];
                    temp = rs.getString(3);
                    ras = temp.split(":");
                    String vrijemeodPravi = ras[0] + ":" + ras[1];
                    temp = rs.getString(4);
                    ras = temp.split(":");
                    String vrijemedoPravi = ras[0] + ":" + ras[1];
                    li.add("\n" + "Zaposlenik: " + korisnickoime + "\nDatum: "
                            + datumPravi + "\nVrijeme od: " + vrijemeodPravi + "\nVrijeme do: "
                            + vrijemedoPravi + "\nOpis: " + rs.getString(5));
                }

                if(li.isEmpty()){
                    TextView internet = (TextView) findViewById(R.id.internet_TraziKM);
                    internet.setText("Nema podataka");
                    internet.setVisibility(View.VISIBLE);
                }
                else{
                    ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list2, li);
                    list.setAdapter(ad);
                }
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Greška kod pretraživanja", Toast.LENGTH_LONG).show();
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) { } // ignore

                con = null;
            }
        }
    }
}