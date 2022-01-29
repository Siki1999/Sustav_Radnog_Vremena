package com.example.sustavradnogvremena;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RadnoVrijemeZaKorisnika extends AppCompatActivity {

    Connection con;
    int tHour, tMinute;
    ArrayList<String> key = new ArrayList<>();
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE && data !=null) {
                String DatumTxt = data.getStringExtra("DatumText");
                TextView datum = (TextView) findViewById(R.id.datum);
                datum.setText(DatumTxt);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radno_vrijeme_za_korisnika);

        Button posalji = (Button) findViewById(R.id.posaljiRV_korisnik);
        TextView internet = (TextView) findViewById(R.id.internetRV_korisnik);
        TextView vrijeme_od = (TextView) findViewById(R.id.vrijeme_od_korisnik);
        TextView vrijeme_do = (TextView) findViewById(R.id.vrijeme_do_korisnik);
        TextView datum = (TextView) findViewById(R.id.datum);
        AutoCompleteTextView korisnici = (AutoCompleteTextView) findViewById(R.id.korisnikDropdown);
        TextInputLayout korisniciError = (TextInputLayout) findViewById(R.id.korisnikDropdownError);

        korisnici.setDropDownBackgroundResource(R.color.autocompletet_background_color);

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

        ArrayList<String> li = new ArrayList<>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Select * from korisnici order by korisnici.korisnickoime asc";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()){
                    li.add(rs.getString(4));
                    key.add(rs.getString(1));
                }

                ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list, li);
                korisnici.setAdapter(ad);
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

        datum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RadnoVrijemeZaKorisnika.this, CalendarActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        vrijeme_od.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        RadnoVrijemeZaKorisnika.this,
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
                        RadnoVrijemeZaKorisnika.this,
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
                if(korisnici.getText().toString().equals("")){
                    korisniciError.setError("Molim odaberite korisnika");
                }
                else{
                    korisniciError.setError(null);
                    int id = li.indexOf(korisnici.getText().toString());
                    DodajRV(id);
                }
            }
        });
    }

    public void DodajRV(int ID){
        TextView datum = findViewById(R.id.datum);
        TextView vrijeme_od = (TextView) findViewById(R.id.vrijeme_od_korisnik);
        TextView vrijeme_do = (TextView) findViewById(R.id.vrijeme_do_korisnik);
        TextView opis = findViewById(R.id.opis_korisnik);
        TextInputLayout opislo = findViewById(R.id.opislo_korisnik);
        String korisnikID = key.get(ID);
        boolean ok = true;

        if(datum.getText().toString().equals("Odaberite datum")){
            datum.setError("Molim odaberite datum");
            ok = false;
        }
        else{
            datum.setError(null);
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
        if(opis.getText().toString().equals("")){
            opislo.setError("Molim unesite kratki opis posla");
            ok = false;
        }
        else{
            opislo.setError(null);
        }
        if(!datum.getText().toString().equals("Odaberite datum")){
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = format.parse(datum.getText().toString());
                Date danasnjiDatum = Calendar.getInstance().getTime();
                if(date.after(danasnjiDatum)){
                    ok = false;
                    Toast.makeText(this,"Molim da odaberete valjani datum", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Greška kod provjere datuma", Toast.LENGTH_LONG).show();
            }
        }

        if(ok){
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                con = connectionHelper.connectionclass();
                if(con!=null){
                    Date date = new SimpleDateFormat("dd-MM-yyyy").parse(datum.getText().toString());
                    String datumProvjera = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    String query = "Select * from radno_vrijeme where datum = '" + datumProvjera + "' and korisnik_id = " + korisnikID;
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if(rs.next()){
                        ok = false;
                        Toast.makeText(this, "Već postoji današnji zapis radnog vremena za tog korisnika", Toast.LENGTH_LONG).show();
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
                                    Date date = new SimpleDateFormat("dd-MM-yyyy").parse(datum.getText().toString());
                                    String datumProvjera = new SimpleDateFormat("yyyy-MM-dd").format(date);
                                    String query = "Insert into radno_vrijeme values (null, '" + datumProvjera + "', '" + vrijeme_od.getText().toString() +
                                            "', '" + vrijeme_do.getText().toString() + "', '" + opis.getText().toString() +
                                            "', '" + korisnikID + "')";
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