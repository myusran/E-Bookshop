package com.emotech.e_bookshop.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.emotech.e_bookshop.Internet.SQLiteHandler;
import com.emotech.e_bookshop.Internet.SessionManager;
import com.emotech.e_bookshop.R;
import com.emotech.e_bookshop.fragment.AkunSayaFragment;
import com.emotech.e_bookshop.fragment.BlankFragment;
import com.emotech.e_bookshop.fragment.KeinginanFragment;
import com.emotech.e_bookshop.fragment.PesananFragment;
import com.emotech.e_bookshop.fragment.HomeFragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager session;
    private SQLiteHandler db;
    private FragmentManager ft = getSupportFragmentManager();
    private NavigationView navigationView;
    private int count;
    private FragmentManager.BackStackEntry backEntry;
    private String namaFragment;
    private static final int PERMISSION_ALL = 1;
    private Menu nav_Menu;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_Menu = navigationView.getMenu();

        if (!session.isLoggedIn()) {
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_pesanan).setVisible(false);
            nav_Menu.findItem(R.id.nav_like).setVisible(false);
            nav_Menu.findItem(R.id.nav_akun).setVisible(false);
            //navigationView.findViewById(R.id.nav_login).setVisibility(View.INVISIBLE);
        }else{
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            //navigationView.findViewById(R.id.nav_logout).setVisibility(View.INVISIBLE);
        }

        ft.beginTransaction().replace(R.id.main_fragment, new HomeFragment()).addToBackStack("Home").commit();
        getSupportActionBar().setTitle("Home");
        navigationView.setCheckedItem(R.id.nav_home);

        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                count = getSupportFragmentManager().getBackStackEntryCount()-1;
                backEntry = getSupportFragmentManager().getBackStackEntryAt(count);
                namaFragment = backEntry.getName();
                if(namaFragment.equalsIgnoreCase("Home")){
                    finish();
                }else{
                    ft.beginTransaction().replace(R.id.main_fragment, new HomeFragment()).addToBackStack("Home").commit();
                    getSupportActionBar().setTitle("Home");
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }else {
                finish();
                //super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            ft.beginTransaction().replace(R.id.main_fragment, new HomeFragment()).addToBackStack("Home").commit();
            getSupportActionBar().setTitle("Home");

        } else if (id == R.id.nav_kategori) {
            ft.beginTransaction().replace(R.id.main_fragment, new BlankFragment()).addToBackStack("Fragment1").commit();
            getSupportActionBar().setTitle("Fragment 1");

        } else if (id == R.id.nav_pesanan) {
            ft.beginTransaction().replace(R.id.main_fragment, new PesananFragment()).addToBackStack("Fragment2").commit();
            getSupportActionBar().setTitle("Pesanan Saya");

        } else if (id == R.id.nav_like) {
            ft.beginTransaction().replace(R.id.main_fragment, new KeinginanFragment()).addToBackStack("Fragment3").commit();
            getSupportActionBar().setTitle("Daftar Keinginan Saya");

        } else if (id == R.id.nav_akun) {
            ft.beginTransaction().replace(R.id.main_fragment, new AkunSayaFragment()).addToBackStack("Fragment4").commit();
            getSupportActionBar().setTitle("Akun Saya");

        } else if (id == R.id.nav_send) {

        }else if(id == R.id.nav_logout){
            logoutUser();
            return true;
        }else if(id == R.id.nav_login){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_pesanan).setVisible(false);
        nav_Menu.findItem(R.id.nav_like).setVisible(false);
        nav_Menu.findItem(R.id.nav_akun).setVisible(false);
        nav_Menu.findItem(R.id.nav_login).setVisible(true);
        drawer.closeDrawers();
        ft.beginTransaction().replace(R.id.main_fragment, new HomeFragment()).addToBackStack("Home").commit();
        getSupportActionBar().setTitle("Home");

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Berhasil Logout")
                .show();
    }
}
