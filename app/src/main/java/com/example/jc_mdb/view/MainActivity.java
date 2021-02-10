package com.example.jc_mdb.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jc_mdb.fragments.FavouriteMovies;
import com.example.jc_mdb.model.tmdb.Discover;
import com.example.jc_mdb.model.tmdb.Genre;
import com.example.jc_mdb.model.tmdb.Movie;
import com.example.jc_mdb.service.FetchFirstTimeDataService;
import com.example.jc_mdb.service.FetchGenresListService;
import com.example.jc_mdb.utils.SearchUtil;
import com.example.jc_mdb.utils.Tools;
import com.google.android.material.navigation.NavigationView;
import com.sg.moviesindex.R;


import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final int REQ_CODE = 100;

    private ProgressBar progressBar;
    public static ArrayList<Movie> movieList = new ArrayList<>();
    private FragmentManager fragmentManager;
    public static int totalPages;
    public static int totalPagesGenres;
    public static int drawer = 0;
    public static int imageup = 0;
    public static long genreid;
    public static String region = "";
    public static int selected;
    public static ArrayList<Discover> discovers;
    public static ArrayList<Genre> genres;
    public static ArrayList<Discover> search;
    public static ArrayList<Movie> moviesearch;
    public static String queryM;
    ImageView navimage;
    SearchView et_search;
    private LinearLayout linearLayoutError;
    private Button refreshButtonError;
    private NavigationView navigationView;
    public FetchGenresListService genresList;
    private FetchFirstTimeDataService fetchFirstTimeDataService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
ImageView voice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tools.setSystemBarColorInt(this, getResources().getColor(R.color.cblue1));
           et_search=findViewById(R.id.et_search);
        et_search.onActionViewExpanded();
et_search.clearFocus();
         et_search.setQueryHint("Search Movie");

        voice = findViewById(R.id.voice);
 load_search();

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fragmentManager = getSupportFragmentManager();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navimage =  findViewById(R.id.navimage);

        navimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        progressBar.animate().alpha(1).setDuration(500);
        progressBar.setIndeterminate(true);
        linearLayoutError = findViewById(R.id.llError);
        refreshButtonError = findViewById(R.id.buttonllError);
        navigationView.getMenu().getItem(0).setChecked(true);
        fetchFirstTimeDataService = new FetchFirstTimeDataService(linearLayoutError, refreshButtonError, progressBar, compositeDisposable, fragmentManager, MainActivity.this);
        fetchFirstTimeDataService.getDataFirst();
        genresList=new FetchGenresListService(linearLayoutError,refreshButtonError, MainActivity.this,compositeDisposable,fetchFirstTimeDataService,progressBar);
    }

    private void load_search() {
        et_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchUtil searchUtil = new SearchUtil(linearLayoutError, refreshButtonError, compositeDisposable, fragmentManager, MainActivity.this, progressBar, fetchFirstTimeDataService);
                searchUtil.search(et_search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchUtil searchUtil = new SearchUtil(linearLayoutError, refreshButtonError, compositeDisposable, fragmentManager, MainActivity.this, progressBar, fetchFirstTimeDataService);
                searchUtil.search(et_search);
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (MainActivity.drawer != 0) {
            MainActivity.drawer = 0;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            navigationView.getMenu().getItem(0).setChecked(true);
            fetchFirstTimeDataService.getDataFirst();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutError.setVisibility(View.GONE);

        int id = item.getItemId();

        if (id == R.id.movies) {
            drawer = 0;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst();
        } else if (id == R.id.favmovies) {
            FragmentTransaction fragmentTransaction1;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            drawer = 5;
            fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.addToBackStack(null);
            fragmentTransaction1.replace(R.id.frame_layout, FavouriteMovies.newInstance()).commit();
        } else if (id == R.id.toprated) {
            drawer = 3;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst();
        } else if (id == R.id.genres) {
            drawer = 4;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            genresList.getGenresList();
        } else if (id == R.id.upcoming_movies) {
            drawer = 2;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst();
        } else if (id == R.id.now_playing) {
            drawer = 1;
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
        case REQ_CODE: {

load_search();
            if (resultCode == RESULT_OK && null != data) {


                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                load_search();

                 et_search.setQuery((result.get(0)), false);


            }
            break;
        }


    }


}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}
