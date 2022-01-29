package com.example.sustavradnogvremena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TraziRV extends AppCompatActivity {

    Connection con;
    ArrayList<String> key = new ArrayList<>();
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE && data !=null) {
                String DatumTxt = data.getStringExtra("DatumText");
                TextView datum = (TextView) findViewById(R.id.datum_RV);
                datum.setText(DatumTxt);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trazi_rv);

        Button trazi = findViewById(R.id.trazi_RV);
        TextView internet = findViewById(R.id.internet_RV_trazenje);
        AutoCompleteTextView korisnici = (AutoCompleteTextView) findViewById(R.id.korisnikDropdown_RV);
        AutoCompleteTextView mjeseci = (AutoCompleteTextView) findViewById(R.id.mjesecDropdown_RV);
        TextView datum = findViewById(R.id.datum_RV);

        korisnici.setDropDownBackgroundResource(R.color.autocompletet_background_color);
        mjeseci.setDropDownBackgroundResource(R.color.autocompletet_background_color);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            trazi.setVisibility(View.VISIBLE);
            internet.setVisibility(View.INVISIBLE);
        }
        else{
            trazi.setVisibility(View.INVISIBLE);
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

        ArrayList<String> mjeseciDropdown = new ArrayList<>();
        YearMonth m = YearMonth.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-yyyy");
        mjeseciDropdown.add(m.format(format));
        for(int i = 1; i<4; i++){
            YearMonth m1 = m.minusMonths(i);
            mjeseciDropdown.add(m1.format(format));
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list, mjeseciDropdown);
        mjeseci.setAdapter(ad);


        datum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TraziRV.this, CalendarActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        trazi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(korisnici.getText().toString().equals("")){
                    TraziRV();
                }
                else{
                    int id = li.indexOf(korisnici.getText().toString());
                    TraziRV(id);
                }
            }
        });
    }

    public void TraziRV( int... ID ){
        String korisnikID = "";
        if(ID.length >= 1){
            korisnikID = key.get(ID[0]);
        }
        AutoCompleteTextView mjeseci = (AutoCompleteTextView) findViewById(R.id.mjesecDropdown_RV);
        TextView datum = findViewById(R.id.datum_RV);
        boolean ok = true;

        if(korisnikID.equals("") && mjeseci.getText().toString().equals("") && datum.getText().toString().equals("Odaberite datum")){
            ok = false;
            Toast.makeText(this,"Molim da odaberete po čemu želite pretraživati", Toast.LENGTH_LONG).show();
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
            if((!korisnikID.equals("")) && (!datum.getText().toString().equals("Odaberite datum"))){
                //trazi korisnika u odredenom datumu
                Intent intent = new Intent(this, TraziKD.class);
                intent.putExtra("Korisnik",korisnikID);
                intent.putExtra("Datum",datum.getText().toString());
                startActivity(intent);
                finish();
            }
            else if((!korisnikID.equals("")) && (!mjeseci.getText().toString().equals(""))){
                //trazi korisnika u mjesecu
                Intent intent = new Intent(this, TraziKM.class);
                intent.putExtra("Korisnik",korisnikID);
                intent.putExtra("Mjesec",mjeseci.getText().toString());
                startActivity(intent);
                finish();
            }
            else if((!korisnikID.equals("")) && datum.getText().toString().equals("Odaberite datum") && mjeseci.getText().toString().equals("")){
                //trazi po korisniku
                Intent intent = new Intent(this, TraziK.class);
                intent.putExtra("Korisnik",korisnikID);
                startActivity(intent);
                finish();
            }
            else if((!datum.getText().toString().equals("Odaberite datum"))){
                //trazi po datumu
                Intent intent = new Intent(this, TraziD.class);
                intent.putExtra("Datum",datum.getText().toString());
                startActivity(intent);
                finish();
            }
            else{
                //trazi po mjesecu
                Intent intent = new Intent(this, TraziM.class);
                intent.putExtra("Mjesec",mjeseci.getText().toString());
                startActivity(intent);
                finish();
            }
        }
    }
}