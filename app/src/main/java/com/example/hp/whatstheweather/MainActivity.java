package com.example.hp.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public void findWeather(View view)
    {
      Log.i("City",editText.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        DownloadTask task = new DownloadTask();



        try{
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&appid=b8ad82be696ffa5bd4f2c7efdb6eebe5").get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public class DownloadTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            URL url = null;
            try {
                url = new URL(urls[0]);
                String result = "";
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data=reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String message="";

            try{
                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Info",weatherInfo);

                JSONArray jsonArray = new JSONArray(weatherInfo);

                for(int i =0;i<jsonArray.length();i++)
                {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main ="";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != "")
                    {
                        message += main + ":" + description + "\r\n";
                    }

                    Log.i("Weather", message);

                    if(message!= "")
                    {
                        resultTextView.setText(message);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }
}
