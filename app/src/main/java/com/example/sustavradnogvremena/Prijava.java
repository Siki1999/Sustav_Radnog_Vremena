package com.example.sustavradnogvremena;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

public class Prijava extends AppCompatActivity {

    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_prijava);

        Button prijava = (Button) findViewById(R.id.prijava);
        TextView internet = (TextView) findViewById(R.id.internet);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            prijava.setVisibility(View.VISIBLE);
            internet.setVisibility(View.INVISIBLE);
        }
        else{
            prijava.setVisibility(View.INVISIBLE);
            internet.setVisibility(View.VISIBLE);
        }

        prijava.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PrijaviSe();
            }
        });
    }

    public void PrijaviSe(){
        TextView korisnickoIme = (TextView) findViewById(R.id.uname);
        TextView sifra = (TextView) findViewById(R.id.lozinka);
        TextInputLayout korisnickoimelo = (TextInputLayout) findViewById(R.id.unamelo);
        TextInputLayout sifralo = (TextInputLayout) findViewById(R.id.lozinkalo);
        boolean ok = true;

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
                    String query = "Select * from korisnici where korisnickoime = '" + korisnickoIme.getText().toString()
                            + "' and sifra = '" + sifra.getText().toString() + "' limit 1";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if(rs.next()){
                        Toast.makeText(this, "Dobrodošli " + korisnickoIme.getText().toString(), Toast.LENGTH_LONG).show();
                        if(rs.getString(6).equals("1")){
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("ID", rs.getString(1));
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(this, RadnoVrijeme.class);
                            intent.putExtra("ID", rs.getString(1));
                            startActivity(intent);
                        }
                    }
                    else{
                        query = "Select * from korisnici where korisnickoime = '" + korisnickoIme.getText().toString()
                                + "' limit 1";
                        st = con.createStatement();
                        rs = st.executeQuery(query);
                        if(rs.next()){
                            Toast.makeText(this, "Pogrešna lozinka", Toast.LENGTH_LONG).show();
                        }
                        else{
                            query = "Select * from korisnici where sifra = '" + sifra.getText().toString() + "' limit 1";
                            st = con.createStatement();
                            rs = st.executeQuery(query);
                            if(rs.next()){
                                Toast.makeText(this, "Pogrešno korisničko ime", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(this, "Korisnik ne postoji", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }catch (Exception ex){
                Toast.makeText(this, "Greška prilikom prijave", Toast.LENGTH_LONG).show();
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