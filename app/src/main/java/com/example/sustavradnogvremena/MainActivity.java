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

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String Id = getIntent().getStringExtra("ID");

        Button sviKorisnici = (Button) findViewById(R.id.SviKorisnici);
        Button noviKorisnik = (Button) findViewById(R.id.NoviKorisnik);
        Button obrisiKorisnik = (Button) findViewById(R.id.ObrisiKorisnika);
        Button dodajRV = (Button) findViewById(R.id.DodajRV);
        Button dodajRVKorisnik = (Button) findViewById(R.id.DodajRVKorisnik);
        Button dodajAdmin = (Button) findViewById(R.id.DodajAdmin);
        Button traziRV = (Button) findViewById(R.id.TraziRV);
        Button izracunaj = (Button) findViewById(R.id.Izracunaj);

        dodajRV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DodajRV(Id);
            }
        });

        traziRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TraziRV();
            }
        });

        izracunaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Izracunaj();
            }
        });

        dodajRVKorisnik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DodajRVKorisnik();
            }
        });

        sviKorisnici.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SviKorisnici();
            }
        });

        noviKorisnik.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NoviKorisnik();
            }
        });

        dodajAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DodajAdmin();
            }
        });

        obrisiKorisnik.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ObrisiKorisnik(Id);
            }
        });
    }

    public void DodajRV( String Id ){
        Intent intent = new Intent(this,RadnoVrijeme.class);
        intent.putExtra("ID", Id);
        startActivity(intent);
    }

    public void TraziRV(){
        Intent intent = new Intent(this, TraziRV.class);
        startActivity(intent);
    }

    public void Izracunaj(){
        Intent intent = new Intent(this, IzracunajZaMjesec.class);
        startActivity(intent);
    }

    public void DodajRVKorisnik(){
        Intent intent = new Intent(this, RadnoVrijemeZaKorisnika.class);
        startActivity(intent);
    }

    public void SviKorisnici(){
        Intent intent = new Intent(this, SviKorisnici.class);
        startActivity(intent);
    }

    public void NoviKorisnik(){
        Intent intent = new Intent(this, NoviKorisnik.class);
        startActivity(intent);
    }

    public void DodajAdmin(){
        Intent intent = new Intent(this, DodajAdmin.class);
        startActivity(intent);
    }

    public void ObrisiKorisnik( String Id ){
        Intent intent = new Intent(this, ObrisiKorisnik.class);
        intent.putExtra("ID", Id);
        startActivity(intent);
    }
}