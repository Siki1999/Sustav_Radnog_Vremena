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

import com.google.android.material.textfield.TextInputLayout;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class IzracunajZaMjesec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izracunaj_za_mjesec);

        Button izracunaj = findViewById(R.id.IzracunajButton);
        TextView internet = findViewById(R.id.internetIzracunaj);
        AutoCompleteTextView mjeseci = (AutoCompleteTextView) findViewById(R.id.mjesecDropdown_Izracunaj);
        TextInputLayout mjeseciError = (TextInputLayout) findViewById(R.id.mjesecDropdownError_Izracunaj);

        mjeseci.setDropDownBackgroundResource(R.color.autocompletet_background_color);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            izracunaj.setVisibility(View.VISIBLE);
            internet.setVisibility(View.INVISIBLE);
        }
        else{
            izracunaj.setVisibility(View.INVISIBLE);
            internet.setVisibility(View.VISIBLE);
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

        izracunaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mjeseci.getText().toString().equals("")){
                    mjeseciError.setError("Molim odaberite mjesec");
                }
                else{
                    mjeseciError.setError(null);
                    Intent intent = new Intent(IzracunajZaMjesec.this, Izracunaj.class);
                    intent.putExtra("Mjesec",mjeseci.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}