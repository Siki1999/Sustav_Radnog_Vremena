package com.example.sustavradnogvremena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SviKorisnici extends AppCompatActivity {

    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svi_korisnici);

        TextView internet = (TextView) findViewById(R.id.internetSviKorisnici);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            internet.setVisibility(View.INVISIBLE);
            GetUsers();
        }
        else{
            internet.setVisibility(View.VISIBLE);
        }
    }

    public void GetUsers(){
        ListView list = (ListView) findViewById(R.id.listView);
        ArrayList<String> li = new ArrayList<>();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Select * from korisnici order by korisnici.ime asc";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()){
                    li.add("\n" + "Zaposlenik: " + rs.getString(2) + " " + rs.getString(3) + "\nKorisnicko ime: "
                            + rs.getString(4) + "\nLozinka: " + rs.getString(5));
                }

                if(li.isEmpty()){
                    TextView internet = (TextView) findViewById(R.id.internetSviKorisnici);
                    internet.setText("Nema podataka");
                    internet.setVisibility(View.VISIBLE);
                }
                else{
                    ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list2, li);
                    list.setAdapter(ad);
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, "Greška kod prikaza svih korisnika", Toast.LENGTH_LONG).show();
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