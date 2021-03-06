package com.emotech.e_bookshop.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emotech.e_bookshop.Activity.Detail_Buku;
import com.emotech.e_bookshop.Activity.MainActivity;
import com.emotech.e_bookshop.Internet.JSONParser;
import com.emotech.e_bookshop.Internet.SQLiteHandler;
import com.emotech.e_bookshop.Internet.UrlConfig;
import com.emotech.e_bookshop.R;
import com.emotech.e_bookshop.adapter.GridViewAdapter;
import com.emotech.e_bookshop.adapter.ImageItem;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    JSONParser jsonParser = new JSONParser();

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private LinearLayout noInternet;
    private SQLiteHandler db;

    private ProgressBar spinner;
    private JSONArray mJsonArray, arRay;

    private ImageItem item;
    private GridLayoutAnimationController controller;

    int udahdiliat = 0;

    public BestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BestFragment newInstance(String param1, String param2) {
        BestFragment fragment = new BestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View rootView = inflater.inflate(R.layout.fragment_best, container, false);

        spinner = (ProgressBar) rootView.findViewById(R.id.bestProgress);
        noInternet = (LinearLayout) rootView.findViewById(R.id.bestnoInternet);
        noInternet.setVisibility(View.GONE);

        gridView = (GridView) rootView.findViewById(R.id.bestgridView);

        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.animation_move);
        controller = new GridLayoutAnimationController(animation, .2f, .2f);
        db = new SQLiteHandler(getActivity());

        if(db.getBestRowCount() > 0) {
            if (db.getBestDateCount() > 0) {
                if (isInternetConnected()) {
                    new getBestSeller() {
                        protected void onPostExecute(JSONArray result) {
                            super.onPostExecute(result);
                            arRay = result;
                            gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData(arRay));
                            gridView.setAdapter(gridAdapter);
                            gridView.setLayoutAnimation(controller);
                            spinner.setVisibility(View.GONE);
                        }
                    }.execute();
                } else {
                    spinner.setVisibility(View.GONE);
                    noInternet.setVisibility(View.VISIBLE);
                }

            }else{
                gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, db.getDataBestSeller());
                gridView.setAdapter(gridAdapter);
                gridView.setLayoutAnimation(controller);
                spinner.setVisibility(View.GONE);
            }
        }else{
            if (isInternetConnected()) {
                new getBestSeller() {
                    protected void onPostExecute(JSONArray result) {
                        super.onPostExecute(result);
                        arRay = result;
                        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData(arRay));
                        gridView.setAdapter(gridAdapter);
                        gridView.setLayoutAnimation(controller);
                        spinner.setVisibility(View.GONE);
                    }
                }.execute();
            } else {
                spinner.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                item = (ImageItem) parent.getItemAtPosition(position);

                if (item.getKodeBuku().equals("")) {
                    showAlertDialog(v.getContext(), "Failed", "Tidak ada data", false);
                }else{
                    Intent i = new Intent(getContext(), Detail_Buku.class);
                    i.putExtra("kode_buku",item.getKodeBuku());
                    startActivity(i);
                }
            }
        });

        return rootView;
    }

    private class getBestSeller extends AsyncTask<Object, Object, JSONArray> {

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            //spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();

                // Fetching user details from sqlite

                JSONObject json = jsonParser.makeHttpRequest(
                        UrlConfig.URL_GET_BEST_SELLER, "POST", param);

                mJsonArray = json.getJSONArray("bestseller");

            }catch (JSONException e) {
                e.printStackTrace();
            }

            return mJsonArray;
        }

    }

    private ArrayList<ImageItem> getData(JSONArray mJsonArray) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        try{
            for (int i = 0; i < mJsonArray.length(); i++) {
                //Bitmap bitmap = capturedImage(mJsonArray.getJSONObject(i).getString("Image"));
                //Bitmap bitmap = getBitmapFromURL(mJsonArray.getJSONObject(i).getString("image"));
                db.addBestSeller(mJsonArray.getJSONObject(i).getString("code"),
                        mJsonArray.getJSONObject(i).getString("title"),
                        mJsonArray.getJSONObject(i).getString("price"),
                        mJsonArray.getJSONObject(i).getString("image"),
                        mJsonArray.getJSONObject(i).getString("UpdateOn"));

                imageItems.add(new ImageItem(mJsonArray.getJSONObject(i).getString("image"),
                        mJsonArray.getJSONObject(i).getString("title"),
                        mJsonArray.getJSONObject(i).getString("price"),
                        mJsonArray.getJSONObject(i).getString("code")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ///} else {
        // showAlertDialog(getActivity(), "Internet Connection",
        //        "Please check your internet connection", false);
        // }

        return imageItems;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    public boolean isNetworksAvailable() {
        ConnectivityManager mConnMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        if (message.equals("Delete Success")) {
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, 10001);
                }
            });
        } else {
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }

        alertDialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    Context con = null;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con = context;
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(con != null) {
            if (isVisibleToUser) {
                if(udahdiliat == 0){
                    udahdiliat = 1;
                }
            }
        }
    }
}
