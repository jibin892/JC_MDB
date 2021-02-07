package com.example.jc_mdb.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.jc_mdb.model.yts.Movie;
import com.example.jc_mdb.repository.Repository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }


    public Movie getMovie(String id) {
        return repository.getMovie(id);
    }

    public LiveData<List<Movie>> getAllMovies() {
        return repository.getAllFMovies();
    }

    public void AddMovie(Movie movie) {

        repository.AddMovie(movie);
    }

    public void DeleteMovie(Movie movie) {
        repository.DeleteMovie(movie);
    }

}
