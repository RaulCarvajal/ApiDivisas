package com.example.raulrcg.apidivisas;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView contenido;
    WServices http;
    String GET_TODO = "http://api.fixer.io/latest?base=MXN";
    String json_string, update;

    double usd,eur;
    EditText mxne,usde,eure;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contenido = (TextView) findViewById(R.id.campo);
        mxne = (EditText) findViewById(R.id.mxn);
        usde = (EditText) findViewById(R.id.usd);
        eure = (EditText) findViewById(R.id.eur);


        http = new WServices();
        http.execute(GET_TODO, "1");
        mxne.setText("1");

        mxne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=1;
            }
        });
        usde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=2;
            }
        });
        eure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=3;
            }
        });

        findViewById(R.id.btnconv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (flag){
                    case 1:
                        double pesos=Double.parseDouble(mxne.getText().toString());
                        usde.setText((pesos*usd+"").substring(0,5));
                        eure.setText((pesos*eur+"").substring(0,5));
                        flag=0;
                        break;

                    case 2:
                        double dolares=Double.parseDouble(usde.getText().toString());
                        mxne.setText((dolares*(1/usd)+"").substring(0,5));
                        eure.setText((1/dolares*eur+"").substring(0,5));
                        flag=0;
                        break;

                    case 3:
                        double euros=Double.parseDouble(eure.getText().toString());
                        usde.setText((1/euros*usd+"").substring(0,5));
                        mxne.setText((euros*(1/eur)+"").substring(0,5));
                        flag=0;
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"Cambie los valores",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    public class WServices extends AsyncTask<String, Void, String> {
        URL url;
        @Override
        protected String doInBackground(String... params) {
            String cadena = "";
            if (params[1]== "1"){
                try {
                    url = new URL(GET_TODO);
                    HttpURLConnection connection = null; // Abrir conexion
                    connection = (HttpURLConnection) url.openConnection();
                    int respuesta = 0;
                    respuesta = connection.getResponseCode();
                    InputStream inputStream = null;
                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        while ((json_string = bufferedReader.readLine()) != null) {
                            stringBuilder.append(json_string + "\n");
                        }
                        bufferedReader.close();
                        inputStream.close();
                        connection.disconnect();
                        String temporal = stringBuilder.toString();
                        JSONObject jsonObj = new JSONObject(temporal);

                        update = jsonObj.getString("date");

                        JSONObject rates = jsonObj.getJSONObject("rates");

                        usd = rates.getDouble("USD");
                        eur = rates.getDouble("EUR");

                        cadena +=update+"/"+usd+"/"+eur;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return  cadena;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String ar[]=s.split("/");
            contenido.setText("Fecha de actualización: "+ar[0]);
            mxne.setText("1");
            usde.setText(ar[1]);
            eure.setText(ar[2]);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
