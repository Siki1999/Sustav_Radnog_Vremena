package com.example.sustavradnogvremena;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ObrisiKorisnik extends AppCompatActivity {

    Connection con;
    ArrayList<String> key = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obrisi_korisnik);

        String Id = getIntent().getStringExtra("ID");

        AutoCompleteTextView dropdown = (AutoCompleteTextView) findViewById(R.id.Dropdown);
        TextInputLayout errorDropdown = (TextInputLayout) findViewById(R.id.ErrorDropdown);
        Button obrisi = (Button) findViewById(R.id.obrisi);
        TextView internet = (TextView) findViewById(R.id.internetBrisiKorisnik);

        dropdown.setDropDownBackgroundResource(R.color.autocompletet_background_color);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            internet.setVisibility(View.INVISIBLE);
            obrisi.setVisibility(View.VISIBLE);
        }
        else{
            internet.setVisibility(View.VISIBLE);
            obrisi.setVisibility(View.INVISIBLE);
        }

        ArrayList<String> li = new ArrayList<>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            con = connectionHelper.connectionclass();
            if(con!=null){
                String query = "Select * from korisnici order by korisnici.ime asc";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()){
                    li.add(rs.getString(4));
                    key.add(rs.getString(1));
                }

                ArrayAdapter<String> ad = new ArrayAdapter<>(getApplicationContext(), R.layout.list, li);
                dropdown.setAdapter(ad);
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

        obrisi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dropdown.getText().toString().equals("")){
                    errorDropdown.setError("Molim odaberite korisnika kako bi ste ga obrisali");
                }
                else{
                    errorDropdown.setError(null);
                    int id = li.indexOf(dropdown.getText().toString());
                    ObrisiKorisnika(id, dropdown.getText().toString(), Id);
                }
            }
        });
    }

    public void ObrisiKorisnika(int ID, String k, String logiraniKorisnik){
        String korisnik = key.get(ID);
        if(korisnik.equals(logiraniKorisnik)){
            Toast.makeText(getApplicationContext(), "Nije moguće obrisati prijavljenog korisnika", Toast.LENGTH_LONG).show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Brisanje");
            builder.setMessage("Jeste li sigurni da želite obrisati korisnika: " + k);
            builder.setCancelable(true);
            builder.setPositiveButton(
                    "DA",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                ConnectionHelper connectionHelper = new ConnectionHelper();
                                con = connectionHelper.connectionclass();
                                if(con!=null){
                                    String query = "Delete from korisnici where id = " + korisnik;
                                    Statement st = con.createStatement();
                                    st.executeUpdate(query);
                                    Toast.makeText(getApplicationContext(), "Uspjeh", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }catch (Exception ex){
                                Toast.makeText(getApplicationContext(), "Greška kod brisanja", Toast.LENGTH_LONG).show();
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