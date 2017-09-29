package com.emotech.e_bookshop.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emotech.e_bookshop.Internet.JSONParser;
import com.emotech.e_bookshop.Internet.SessionManager;
import com.emotech.e_bookshop.Internet.UrlConfig;
import com.emotech.e_bookshop.R;
import com.emotech.e_bookshop.adapter.GridViewAdapter;
import com.emotech.e_bookshop.fragment.BukuFragment;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Detail_Buku extends AppCompatActivity {

    private SessionManager session;
    private LinearLayout data;
    private ProgressBar spinner;
    private TextView judul, penulis, isbn, penerbit, harga;
    private WebView deskripsi;
    private ImageView gambar;
    private JSONObject mJsonObject, arRay;
    private String kode_buku;
    private Button keranjang, like;

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__buku);

        Intent intent = getIntent();
        kode_buku = intent.getStringExtra("kode_buku");

        data = (LinearLayout) findViewById(R.id.layout_detail_buku);
        data.setVisibility(View.GONE);

        // session manager
        session = new SessionManager(getApplicationContext());

        spinner = (ProgressBar) findViewById(R.id.progressBarXXX);
        // Get the Drawable custom_progressbar
        Drawable draw = getResources().getDrawable(R.drawable.circular_progress_bar);
        // set the drawable as progress drawable
        spinner.setProgressDrawable(draw);


        judul = (TextView) findViewById(R.id.judul);
        penulis = (TextView) findViewById(R.id.penulis);
        isbn = (TextView) findViewById(R.id.isbn);
        penerbit = (TextView) findViewById(R.id.penerbit);
        harga = (TextView) findViewById(R.id.harga);
        deskripsi = (WebView) findViewById(R.id.deskripsi);
        gambar = (ImageView) findViewById(R.id.image);
        keranjang = (Button) findViewById(R.id.button2);
        like = (Button) findViewById(R.id.button3);

        if (!session.isLoggedIn()) {
            like.setVisibility(View.INVISIBLE);
        }

        if (isInternetConnected()) {
            new getDetailBuku(){
                protected void onPostExecute(JSONObject result) {
                    super.onPostExecute(result);
                    arRay = result;
                    setDetailBuku(arRay);
                    data.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                }
            }.execute();

        }else {
            spinner.setVisibility(View.GONE);
            new SweetAlertDialog(Detail_Buku.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Tidak Ada Koneksi")
                    .setConfirmText("Ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // Launch login activity
                            Intent intent = new Intent(
                                    Detail_Buku.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }

    }

    private class getDetailBuku extends AsyncTask<Object, Object, JSONObject>{

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();

                param.add(new BasicNameValuePair("kode_buku", kode_buku.trim()));

                // Fetching user details from sqlite

                JSONObject json = jsonParser.makeHttpRequest(
                        UrlConfig.URL_GET_DETAIL_BUKU, "POST", param);

                mJsonObject = json.getJSONObject("detail");

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Async Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return mJsonObject;
        }
    }

    private void setDetailBuku(JSONObject detail){

        try{

            //JSONObject buku = detail.getJSONObject("detail");
            judul.setText(detail.getString("judul"));
            penulis.setText(detail.getString("penulis"));
            isbn.setText("E-ISBN : "+detail.getString("isbn"));
            penerbit.setText("Penerbit : "+detail.getString("penerbit"));
            harga.setText(detail.getString("harga"));
            String summary = "<html><body>"+detail.getString("deskripsi")+"</body></html>";
            deskripsi.loadData(summary, "text/html", null);
            Picasso.with(this).load(detail.getString("gambar")).into(gambar);
            //gambar.setText(buku.getString("gambar"));

        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworksAvailable() {
        ConnectivityManager mConnMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr != null) {
            NetworkInfo[] mNetInfo = mConnMgr.getAllNetworkInfo();
            if (mNetInfo != null) {
                for (int i = 0; i < mNetInfo.length; i++) {
                    if (mNetInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInternetConnected() {
        final int CONNECTION_TIMEOUT = 1500;
        if (isNetworksAvailable()) {
            try {
                HttpURLConnection mURLConnection = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                mURLConnection.setRequestProperty("User-Agent", "Android");
                mURLConnection.setRequestProperty("Connection", "close");
                mURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                mURLConnection.setReadTimeout(CONNECTION_TIMEOUT);
                mURLConnection.connect();
                return (mURLConnection.getResponseCode() == 204 && mURLConnection.getContentLength() == 0);
            } catch (IOException ioe) {
                Log.e("isInternetConnected", "Exception occured while checking for Internet connection: ", ioe);
            }
        } else {
            Log.e("isInternetConnected", "Not connected to WiFi/Mobile and no Internet available.");
        }
        return false;
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);


        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
}