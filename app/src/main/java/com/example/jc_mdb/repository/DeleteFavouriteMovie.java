package com.example.jc_mdb.repository;

import android.os.AsyncTask;

import com.example.jc_mdb.db.FavouriteMoviesDAO;
import com.example.jc_mdb.model.tmdb.Movie;

public class DeleteFavouriteMovie extends AsyncTask<Movie, Void, Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public DeleteFavouriteMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.deleteFMovie(movies[0]);
        return null;
    }
}
