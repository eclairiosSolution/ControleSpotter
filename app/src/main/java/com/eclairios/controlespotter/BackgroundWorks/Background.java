package com.eclairios.controlespotter.BackgroundWorks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.eclairios.controlespotter.Others.Constants.delete_place;
import static com.eclairios.controlespotter.Others.Constants.insert_place;
import static com.eclairios.controlespotter.Others.Constants.nearplaces;
import static com.eclairios.controlespotter.Others.Constants.read_category;
import static com.eclairios.controlespotter.Others.Constants.read_category_id;
import static com.eclairios.controlespotter.Others.Constants.read_place;
import static com.eclairios.controlespotter.Others.Constants.update_place;

public class Background extends AsyncTask<String, Void, String> {
    String result;

    private Context ctx;

    private ProgressDialog progress;


    public Background(Context ctx) {
        Log.e("entetinbackground", "Background:  constructor");
        this.ctx = ctx;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("checkprogressbarvisi", "onPreExecute: ");

//        progress.show();

    }

    @Override
    protected String doInBackground(String... params) {



        String method = params[0];


        if (method.equals("insert_place")) {

            Log.e("entetinbackground", "doInBackground: --- insert_place");

            return insert_place_method(insert_place, params);

        }
        else if (method.equals("delete_place")) {

            Log.e("entetinbackground", "doInBackground: --- delete_place");

            return delete_place_method(delete_place, params);

        } else if (method.equals("read_place")) {

            Log.e("entetinbackground", "doInBackground: --- read_place");

            return readplace_method(read_place,params);

        }else if (method.equals("update_place")){

            Log.e("entetinbackground", "doInBackground: --- update_place");

            return update_place_method(update_place,params);

        }else if (method.equals("read_category")){

            try {
                URL url = new URL(read_category);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

                String response3 = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response3 += line;

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                result = response3;
                return response3;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }else if (method.equals("near_place")){

            String lat = params[1];
            String lng = params[2];
            String radius = params[3];
            String userid = params[4];

            try {
                URL url = new URL(nearplaces);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8")
                        + "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8")
                        + "&" + URLEncoder.encode("radius", "UTF-8") + "=" + URLEncoder.encode(radius, "UTF-8")
                        + "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

                String response3 = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response3 += line;

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                result = response3;
                return response3;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        else if (method.equals("read_category_id")){

            String id = params[1];

            try {
                URL url = new URL(read_category_id);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

                String response3 = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response3 += line;

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                result = response3;
                return response3;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        return null;

    }



    @Nullable
    private String insert_place_method(String create_user, String[] params) {

        String stringname = params[1];
        String stringaddress = params[2];
        String stringlat = params[3];
        String stringlng = params[4];
        String radius = params[5];
        String android_id = params[6];
        String category = params[7];

        try {
            URL url = new URL(create_user);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(android_id, "UTF-8")
                    + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(stringlat, "UTF-8")
                    + "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(stringlng, "UTF-8")
                    + "&" + URLEncoder.encode("radius", "UTF-8") + "=" + URLEncoder.encode(radius, "UTF-8")
                    + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(stringname, "UTF-8")
                    + "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(stringaddress, "UTF-8")
                    + "&" + URLEncoder.encode("categoryid", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

            String response3 = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                response3 += line;

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            result = response3;
            Log.e("fdaksfhkjashdf", "insert_place_method: "+response3 );
            return response3;



        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    private String update_place_method(String create_user, String[] params) {

        String stringname = params[1];
        String stringaddress = params[2];
        String stringlat = params[3];
        String stringlng = params[4];
        String radius = params[5];
        String android_id = params[6];
        String category = params[7];
        String id = params[8];
        String userstatus = params[9];

        try {
            URL url = new URL(create_user);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                    + "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(android_id, "UTF-8")
                    + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(stringlat, "UTF-8")
                    + "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(stringlng, "UTF-8")
                    + "&" + URLEncoder.encode("radius", "UTF-8") + "=" + URLEncoder.encode(radius, "UTF-8")
                    + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(stringname, "UTF-8")
                    + "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(stringaddress, "UTF-8")
                    + "&" + URLEncoder.encode("categoryid", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8")
                    + "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(userstatus, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

            String response3 = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                response3 += line;

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            result = response3;
            return response3;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    private String readplace_method(String read_clientdetail, String[] params) {

        String android_id = params[1];

        try {
            URL url = new URL(read_clientdetail);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(android_id, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

            String response4 = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                response4 += line;

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            result = response4;
            Log.e("kjfgldfgdaf", "readplace_method: "+result );
            return response4;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    private String delete_place_method(String read_clientdetail, String[] params) {

        String Id = params[1];
        String userid = params[2];

        try {
            URL url = new URL(read_clientdetail);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8")
                    + "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

            String response4 = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                response4 += line;

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            result = response4;
            return response4;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {

//        progress.dismiss();
    }

}