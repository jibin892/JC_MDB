package com.example.jc_mdb.service;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jc_mdb.config.BuildConfigs;
import com.example.jc_mdb.fragments.Movies;
import com.example.jc_mdb.model.tmdb.Discover;
import com.example.jc_mdb.model.tmdb.DiscoversList;
import com.example.jc_mdb.model.tmdb.Movie;
import com.example.jc_mdb.model.tmdb.MoviesList;
import com.example.jc_mdb.service.network.RetrofitInstance;
import com.example.jc_mdb.service.network.TMDbService;
import com.example.jc_mdb.utils.DiscoverToMovie;
import com.example.jc_mdb.utils.SearchUtil;
import com.example.jc_mdb.view.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sg.moviesindex.R;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FetchFirstTimeDataService implements Parcelable {
    private ProgressBar progressBar;
    private Observable<MoviesList> observableMovie;
    private CompositeDisposable compositeDisposable;
    private FragmentManager fragmentManager;
    private LinearLayout linearLayoutError;
    private Button refreshButtonError;
    private FragmentTransaction fragmentTransaction;
    private Context context;
    public SearchUtil searchUtil;
    public FetchGenresListService fetchGenresListService;


    public FetchFirstTimeDataService(LinearLayout linearLayout, Button button, ProgressBar progressBar, CompositeDisposable compositeDisposable, FragmentManager fragmentManager, Context context) {
        this.progressBar = progressBar;
        this.linearLayoutError = linearLayout;
        this.refreshButtonError = button;
        this.compositeDisposable = compositeDisposable;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.searchUtil = new SearchUtil(linearLayout, button, compositeDisposable, fragmentManager, context, progressBar, this);
        this.fetchGenresListService = new FetchGenresListService(linearLayout, button, context, compositeDisposable, this, progressBar);
        this.fragmentTransaction = fragmentManager.beginTransaction();
    }

    protected FetchFirstTimeDataService(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FetchFirstTimeDataService> CREATOR = new Creator<FetchFirstTimeDataService>() {
        @Override
        public FetchFirstTimeDataService createFromParcel(Parcel in) {
            return new FetchFirstTimeDataService(in);
        }

        @Override
        public FetchFirstTimeDataService[] newArray(int size) {
            return new FetchFirstTimeDataService[size];
        }
    };

    public void getDataFirst() {
        int a = MainActivity.drawer;
        final TMDbService TMDbService = RetrofitInstance.getTMDbService(context);
        String ApiKey = BuildConfigs.apiKey;
        if (a == 0 || a == 3) {
            if (a == 0) {
                observableMovie = TMDbService.getPopularMoviesWithRx(ApiKey, 1);
            } else if (a == 3) {
                observableMovie = TMDbService.getTopRatedMoviesWithRx(ApiKey, 1);
            }
            fetchData();
        } else if (a == 1 || a == 2) {
            String[] x = {"All", "India", "USA", "UK"};

                        MainActivity.region = "";
                        fetchData();
        }


    }

    private void fetchData() {
        int a = MainActivity.drawer;
        compositeDisposable.add(
                observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<MoviesList>() {
                            @Override
                            public void onNext(@NotNull MoviesList moviesList) {
                                if (moviesList.getMovies() != null) {
                                    MainActivity.movieList = (ArrayList<Movie>) moviesList.getMovies();
                                    MainActivity.totalPages = moviesList.getTotalPages();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    if (fragmentManager.getFragments().isEmpty()) {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.add(R.id.frame_layout, Movies.newInstance(FetchFirstTimeDataService.this, searchUtil)).commit();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_layout, Movies.newInstance(FetchFirstTimeDataService.this, searchUtil)).commit();
                                    }
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisibility(View.GONE);
                                linearLayoutError.setVisibility(View.VISIBLE);
                                refreshButtonError.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar.setIndeterminate(true);
                                        progressBar.setVisibility(View.VISIBLE);
                                        linearLayoutError.setVisibility(View.GONE);
                                        getDataFirst();
                                    }
                                });
                                Log.d("Check Your Internet", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));
    }


    public void getFirstGenreData() {
        final TMDbService TMDbService = RetrofitInstance.getTMDbService(context);
        String ApiKey = BuildConfigs.apiKey;
        Observable<DiscoversList> observableDB = TMDbService.discover(ApiKey, Long.toString(MainActivity.genreid), false, false, 1, "popularity.desc", null, null, null, null).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                progressBar.setIndeterminate(false);
            }
        });
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DiscoversList>() {
                            @Override
                            public void onNext(@NotNull DiscoversList discoversList) {
                                if (discoversList.getResults() != null) {
                                    MainActivity.discovers = (ArrayList<Discover>) discoversList.getResults();
                                    MainActivity.totalPagesGenres = discoversList.getTotalPages();
//                                    DiscoverToMovie discoverToMovie = new DiscoverToMovie(MainActivity.discovers);
//                                    MainActivity.movieList = discoverToMovie.getMovies();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.add(R.id.frame_layout, Movies.newInstance(FetchFirstTimeDataService.this, searchUtil)).commit();
                                }

                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisibility(View.GONE);
                                linearLayoutError.setVisibility(View.VISIBLE);
                                refreshButtonError.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar.setIndeterminate(true);
                                        progressBar.setVisibility(View.VISIBLE);
                                        linearLayoutError.setVisibility(View.GONE);
                                        getFirstGenreData();
                                    }
                                });
                                Log.d("Check Your Internet", e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
