package com.example.sustavradnogvremena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NoviKorisnik extends AppCompatActivity {

    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novi_korisnik);

        Button posalji = (Button) findViewById(R.id.posalji);
        TextView internet = (TextView) findViewById(R.id.internetNoviKorisnik);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            posalji.setVisibility(View.VISIBLE);
            internet.setVisibility(View.INVISIBLE);
        }
        else{
            posalji.setVisibility(View.INVISIBLE);
            internet.setVisibility(View.VISIBLE);
        }

        posalji.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DodajKorisnika();
            }
        });
    }

    public void DodajKorisnika(){
        TextView ime = (TextView) findViewById(R.id.ime);
        TextView prezime = (TextView) findViewById(R.id.prezime);
        TextView korisnickoIme = (TextView) findViewById(R.id.korisnickoime);
        TextView sifra = (TextView) findViewById(R.id.sifra);
        TextInputLayout imelo = (TextInputLayout) findViewById(R.id.imelo);
        TextInputLayout prezimelo = (TextInputLayout) findViewById(R.id.prezimelo);
        TextInputLayout korisnickoimelo = (TextInputLayout) findViewById(R.id.korisnickoimelo);
        TextInputLayout sifralo = (TextInputLayout) findViewById(R.id.sifralo);
        boolean ok = true;
        if(ime.getText().toString().equals("")){
            imelo.setError("Molim unesite ime");
            ok = false;
        }
        else{
            imelo.setError(null);
        }
        if(prezime.getText().toString().equals("")){
            prezimelo.setError("Molim unesite prezime");
            ok = false;
        }
        else{
            prezimelo.setError(null);
        }
        if(korisnickoIme.getText().toString().equals("")){
            korisnickoimelo.setError("Molim unesite korisnićko ime");
            ok = false;
        }
        else{
            korisnickoimelo.setError(null);
        }
        if(sifra.getText().toString().equals("")){
            sifralo.setError("Molim unesite šifru");
            ok = false;
        }
        else{
            sifralo.setError(null);
        }

        if(ok){
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                con = connectionHelper.connectionclass();
                if(con!=null){
                    String query = "Select * from korisnici where korisnickoime = '" + korisnickoIme.getText().toString() + "'";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if(rs.next()){
                        ok = false;
                        Toast.makeText(this, "Korisnićko ime već postoji", Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Exception ex){
                Toast.makeText(this, "Greška kod provjere korisnickog imena", Toast.LENGTH_LONG).show();
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

        if(ok){
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                con = connectionHelper.connectionclass();
                if(con!=null){
                    String query = "Insert into korisnici values (null, '"+ime.getText().toString()+"'," +
                            " '"+prezime.getText().toString()+"', '"+korisnickoIme.getText().toString()+
                            "', '"+sifra.getText().toString()+"', 0)";
                    Statement st = con.createStatement();
                    st.executeUpdate(query);
                    Toast.makeText(this, "Uspjeh", Toast.LENGTH_LONG).show();
                    finish();
                }
            }catch (Exception ex){
                Toast.makeText(this, "Greška kod unosa", Toast.LENGTH_LONG).show();
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
}