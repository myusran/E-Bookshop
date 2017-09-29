package com.emotech.e_bookshop.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.emotech.e_bookshop.Activity.Detail_Buku;
import com.emotech.e_bookshop.Internet.JSONParser;
import com.emotech.e_bookshop.Internet.SQLiteHandler;
import com.emotech.e_bookshop.Internet.UrlConfig;
import com.emotech.e_bookshop.R;
import com.emotech.e_bookshop.adapter.GridViewAdapter;
import com.emotech.e_bookshop.adapter.ImageItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KeinginanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KeinginanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeinginanFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ProgressBar spinner;
    private ImageItem item;
    private JSONArray mJsonArray, arRay;
    private ImageView imageview;
    private SQLiteHandler db;
    private HashMap<String, String> user;
    JSONParser jsonParser = new JSONParser();

    private GridLayoutAnimationController controller;

    private OnFragmentInteractionListener mListener;

    public KeinginanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KeinginanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KeinginanFragment newInstance(String param1, String param2) {
        KeinginanFragment fragment = new KeinginanFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_keinginan, container, false);

        db = new SQLiteHandler(getActivity());
        user = db.getUserDetails();

        spinner = (ProgressBar) rootView.findViewById(R.id.keinginanprogressBar);

        imageview = (ImageView) rootView.findViewById(R.id.keinginanimageView2);
        imageview.setVisibility(View.GONE);

        gridView = (GridView) rootView.findViewById(R.id.keinginangridView);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_move);
        final GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);


        if (isInternetConnected()) {
            new getBukueinginan() {
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
            imageview.setVisibility(View.VISIBLE);
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                item = (ImageItem) parent.getItemAtPosition(position);

                if (item.getKodeBuku().equals("")) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Failed Tidak ada data")
                            .show();
                } else {
                    Intent i = new Intent(getContext(), Detail_Buku.class);
                    i.putExtra("kode_buku", item.getKodeBuku());
                    startActivity(i);
                }
            }
        });

        return rootView;
    }

    private class getBukueinginan extends AsyncTask<Object, Object, JSONArray> {

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            //spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("email", user.get("email")));

                // Fetching user details from sqlite

                JSONObject json = jsonParser.makeHttpRequest(
                        UrlConfig.URL_GET_KEINGINAN_BUKU, "POST", param);

                mJsonArray = json.getJSONArray("bukukeinginan");

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
}
