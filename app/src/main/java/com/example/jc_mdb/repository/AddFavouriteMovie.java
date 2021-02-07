package com.example.jc_mdb.repository;

import android.os.AsyncTask;

import com.example.jc_mdb.db.FavouriteMoviesDAO;
import com.example.jc_mdb.model.tmdb.Movie;

public class AddFavouriteMovie extends AsyncTask<Movie, Void, Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public AddFavouriteMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.insertFMovie(movies[0]);
        return null;
    }
}
