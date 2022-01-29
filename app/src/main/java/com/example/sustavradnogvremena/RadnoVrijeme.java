package com.example.sustavradnogvremena;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RadnoVrijeme extends AppCompatActivity {

    Connection con;
    int tHour, tMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radno_vrijeme);

        String Id = getIntent().getStringExtra("ID");

        Button posalji = (Button) findViewById(R.id.posaljiRV);
        TextView internet = (TextView) findViewById(R.id.internetRV);
        TextView vrijeme_od = (TextView) findViewById(R.id.vrijeme_od);
        TextView vrijeme_do = (TextView) findViewById(R.id.vrijeme_do);

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

        vrijeme_od.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        RadnoVrijeme.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                tHour = i;
                                tMinute = i1;
                                String time = tHour + ":" + tMinute;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = simpleDateFormat.parse(time);
                                    vrijeme_od.setText(simpleDateFormat.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 24, 0 ,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour,tMinute);
                timePickerDialog.setTitle("Početak rada");
                timePickerDialog.show();
            }
        });

        vrijeme_do.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        RadnoVrijeme.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                tHour = i;
                                tMinute = i1;
                                String time = tHour + ":" + tMinute;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = simpleDateFormat.parse(time);
                                    vrijeme_do.setText(simpleDateFormat.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 24, 0 ,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour,tMinute);
                timePickerDialog.setTitle("Kraj rada");
                timePickerDialog.show();
            }
        });

        posalji.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DodajRV(Id);
            }
        });
    }

    public void DodajRV( String Id ){
        TextView vrijeme_od = (TextView) findViewById(R.id.vrijeme_od);
        TextView vrijeme_do = (TextView) findViewById(R.id.vrijeme_do);
        TextView opis = (TextView) findViewById(R.id.opis);
        TextInputLayout opislo = (TextInputLayout) findViewById(R.id.opislo);
        boolean ok = true;

        if(opis.getText().toString().equals("")){
            opislo.setError("Molim unesite kratki opis posla");
            ok = false;
        }
        else{
            opislo.setError(null);
        }
        if(vrijeme_od.getText().toString().equals("Odaberite vrijeme")){
            vrijeme_od.setError("Molim odaberite početak rada");
            ok = false;
        }
        else{
            vrijeme_od.setError(null);
        }
        if(vrijeme_do.getText().toString().equals("Odaberite vrijeme")){
            vrijeme_do.setError("Molim odaberite kraj rada");
            ok = false;
        }
        else{
            vrijeme_do.setError(null);
        }

        if(ok){
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                con = connectionHelper.connectionclass();
                if(con!=null){
                    String datum = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String query = "Select * from radno_vrijeme where datum = '" + datum + "' and korisnik_id = " + Id;
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if(rs.next()){
                        ok = false;
                        Toast.makeText(this, "Već postoji današnji zapis radnog vremena", Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Exception ex){
                Toast.makeText(this, "Greška kod provjere zapisa", Toast.LENGTH_LONG).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Molim provjerite još jednom podatke");
            builder.setMessage("Jeste li sigurni da želite poslati podatke");
            builder.setCancelable(true);
            builder.setPositiveButton(
                    "DA",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                ConnectionHelper connectionHelper = new ConnectionHelper();
                                con = connectionHelper.connectionclass();
                                if(con!=null){
                                    String datum = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    String query = "Insert into radno_vrijeme values (null, '" + datum + "', '" + vrijeme_od.getText().toString() +
                                            "', '" + vrijeme_do.getText().toString() + "', '" + opis.getText().toString() +
                                            "', '" + Id + "')";
                                    Statement st = con.createStatement();
                                    st.executeUpdate(query);
                                    Toast.makeText(getApplicationContext(), "Uspjeh", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }catch (Exception ex){
                                Toast.makeText(getApplicationContext(), "Greška kod unosa", Toast.LENGTH_LONG).show();
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
                    });
            builder.setNegativeButton(
                    "NE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    }
}